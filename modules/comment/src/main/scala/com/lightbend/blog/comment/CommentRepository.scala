package com.lightbend.blog.comment

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

/**
 * A pure non-blocking interface for the Comment Repository.
 */
trait CommentRepository {

  def findByPost(postId: String): Future[Iterable[CommentData]]

  def list(): Future[Iterable[CommentData]]

  def get(id: CommentId): Future[Option[CommentData]]

}

/**
 * Controls any resources owned by the comment repository.
 */
trait CommentRepositoryLifecycle {

  def stop(): Future[Unit]

}

/**
 * A typed execution context for the Comment Repository.
 *
 * An execution context provides access to an Executor, but it's important
 * that the thread pool is sized appropriately to the underlying implementation.
 * For example, if you are using JDBC or a similar blocking model, then you will
 * need a ThreadPoolExecutor with a fixed size equal to the maximum number of JDBC
 * connections in the JDBC connection pool (i.e. HirakiCP).
 *
 * Because ExecutionContext is often passed round implicitly and it's not widely
 * known, it's much better to ensure that anything Repository based has a custom
 * strongly typed execution context so that an inappropriate ExecutionContext can't
 * be used by accident.
 */
class CommentExecutionContext(val underlying: ExecutionContext) extends AnyVal

/**
 * A trivial implementation for the Comment Repository.
 *
 * The execution context is injected here and used with live Future (rather than
 * Future.successful) to show how you would use this in a blocking or I/O bound
 * implementation.
 */
@Singleton
class CommentRepositoryImpl @Inject()(cec: CommentExecutionContext)
  extends CommentRepository with CommentRepositoryLifecycle {
  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private implicit val ec: ExecutionContext = cec.underlying

  private val commentList = List(
    CommentData(CommentId("1"), postId = "1", "comment 1"),
    CommentData(CommentId("2"), postId = "1", "comment 2"),
    CommentData(CommentId("3"), postId = "3", "comment 3"),
    CommentData(CommentId("4"), postId = "3", "comment 4"),
    CommentData(CommentId("5"), postId = "5", "comment 5")
  )

  override def list(): Future[Iterable[CommentData]] = {
    Future {
      logger.trace("list: ")
      commentList
    }
  }

  override def findByPost(postId: String): Future[Iterable[CommentData]] = {
    Future {
      logger.trace(s"findByPost: postId = $postId")
      commentList.filter(comment => comment.postId == postId)
    }
  }

  override def get(id: CommentId): Future[Option[CommentData]] = {
    Future {
      logger.trace(s"get: id = $id")
      commentList.find(comment => comment.id == id)
    }
  }

  override def stop(): Future[Unit] = {
    Future {
      ()
    }
  }
}


