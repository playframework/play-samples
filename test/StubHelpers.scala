import java.nio.file.Path

import akka.actor.Cancellable
import akka.stream.{Attributes, ClosedShape, Graph, Materializer}
import play.api.http._
import play.api.i18n.{DefaultLangs, DefaultMessagesApi, Langs, MessagesApi}
import play.api.libs.Files
import play.api.libs.streams.Accumulator
import play.api.mvc._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.Try

trait StubPlayBodyParsersFactory {

  /**
   * Stub method for unit testing, using NoTemporaryFileCreator.
   *
   * @param mat the input materializer.
   * @return a minimal PlayBodyParsers for unit testing.
   */
  def stubPlayBodyParsers(mat: Materializer): PlayBodyParsers = {
    val errorHandler = new DefaultHttpErrorHandler(HttpErrorConfig(showDevErrors = false, None), None, None)
    PlayBodyParsers(ParserConfiguration(), errorHandler, mat, NoTemporaryFileCreator)
  }

}

trait StubBodyParserFactory {
  /**
   * Stub method that returns the content immediately.  Useful for unit testing.
   *
   * {{{
   * val stubParser = bodyParser(AnyContent("hello"))
   * }}}
   *
   * @param content the content to return, AnyContentAsEmpty by default
   * @return a BodyParser for type T that returns Accumulator.done(Right(content))
   */
  def stubBodyParser[T](content: T = AnyContentAsEmpty): BodyParser[T] = {
    BodyParser(_ => Accumulator.done(Right(content)))
  }
}

trait StubControllerComponentsFactory extends StubPlayBodyParsersFactory with StubBodyParserFactory {

  /**
   * Create a minimal controller components, useful for unit testing.
   *
   * In most cases, you'll want the standard defaults:
   *
   * {{{
   *   val controller = new MyController(stubControllerComponents())
   * }}}
   *
   * A custom body parser can be used with bodyParser() to provide a request body to the controller:
   *
   * {{{
   * val cc = stubControllerComponents(bodyParser(AnyContent("request body text")))
   * }}}
   *
   * @param bodyParser the body parser used to parse any content, stubBodyParser(AnyContentAsEmpty) by default.
   * @param playBodyParsers the playbodyparsers, defaults to stubPlayBodyParsers(NoMaterializer)
   * @param messagesApi: the messages api, new DefaultMessagesApi() by default.
   * @param langs the langs instance for messaging, new DefaultLangs() by default.
   * @param fileMimeTypes the mime type associated with file extensions, new DefaultFileMimeTypes(FileMimeTypesConfiguration() by default.
   * @param executionContent an execution context, defaults to ExecutionContext.global
   * @return a fully configured ControllerComponents instance.
   */
  def stubControllerComponents(
                                bodyParser: BodyParser[AnyContent] = stubBodyParser(AnyContentAsEmpty),
                                playBodyParsers: PlayBodyParsers = stubPlayBodyParsers(NoMaterializer),
                                messagesApi: MessagesApi = new DefaultMessagesApi(),
                                langs: Langs = new DefaultLangs(),
                                fileMimeTypes: FileMimeTypes = new DefaultFileMimeTypes(FileMimeTypesConfiguration()),
                                executionContent: ExecutionContext = ExecutionContext.global): ControllerComponents = {
    DefaultControllerComponents(
      DefaultActionBuilder(bodyParser)(executionContent),
      playBodyParsers,
      messagesApi,
      langs,
      fileMimeTypes,
      executionContent)
  }
}

/**
 * A temporary file creator with no implementation.
 */
object NoTemporaryFileCreator extends Files.TemporaryFileCreator {
  override def create(prefix: String, suffix: String): Files.TemporaryFile = {
    throw new UnsupportedOperationException("Cannot create temporary file")
  }
  override def create(path: Path): Files.TemporaryFile = {
    throw new UnsupportedOperationException(s"Cannot create temporary file at $path")
  }
  override def delete(file: Files.TemporaryFile): Try[Boolean] = {
    throw new UnsupportedOperationException(s"Cannot delete temporary file at $file")
  }
}

/**
 * In 99% of cases, when running tests against the result body, you don't actually need a materializer since it's a
 * strict body. So, rather than always requiring an implicit materializer, we use one if provided, otherwise we have
 * a default one that simply throws an exception if used.
 */
object NoMaterializer extends Materializer {
  override def withNamePrefix(name: String): Materializer =
    throw new UnsupportedOperationException("NoMaterializer cannot be named")
  override def materialize[Mat](runnable: Graph[ClosedShape, Mat]): Mat =
    throw new UnsupportedOperationException("NoMaterializer cannot materialize")
  override def materialize[Mat](runnable: Graph[ClosedShape, Mat], initialAttributes: Attributes): Mat =
    throw new UnsupportedOperationException("NoMaterializer cannot materialize")

  override def executionContext: ExecutionContextExecutor =
    throw new UnsupportedOperationException("NoMaterializer does not provide an ExecutionContext")

  def scheduleOnce(delay: FiniteDuration, task: Runnable): Cancellable =
    throw new UnsupportedOperationException("NoMaterializer cannot schedule a single event")

  def schedulePeriodically(initialDelay: FiniteDuration, interval: FiniteDuration, task: Runnable): Cancellable =
    throw new UnsupportedOperationException("NoMaterializer cannot schedule a repeated event")
}
