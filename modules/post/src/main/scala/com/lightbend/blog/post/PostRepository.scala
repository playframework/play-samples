package com.lightbend.blog.post

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

/**
 * A pure non-blocking interface for the PostRepository.
 */
trait PostRepository {

  def list(): Future[Iterable[PostData]]

  def get(id: PostId): Future[Option[PostData]]

  def stop(): Future[Unit]
}

/**
 * Controls any resources owned by the post repository.
 */
trait PostRepositoryLifecycle {

  def stop(): Future[Unit]

}

/**
 * A typed execution context for the PostRepository.
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
class PostExecutionContext(val underlying: ExecutionContext) extends AnyVal

/**
 * A trivial implementation for the Post Repository.
 *
 * The execution context is injected here and used with live Future (rather than
 * Future.successful) to show how you would use this in a blocking or I/O bound
 * implementation.
 */
@Singleton
class PostRepositoryImpl @Inject()(pec: PostExecutionContext) extends PostRepository with PostRepositoryLifecycle {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private implicit val ec: ExecutionContext = pec.underlying

  private val postList = List(
    PostData(PostId("1"), "title 1", "blog post 1"),
    PostData(PostId("2"), "title 2", "blog post 2"),
    PostData(PostId("3"), "title 3", "blog post 3"),
    PostData(PostId("4"), "title 4", "blog post 4"),
    PostData(PostId("5"), "title 5", "blog post 5")
  )

  override def list(): Future[Iterable[PostData]] = {
    Future {
      logger.trace("list: ")
      postList
    }
  }

  override def get(id: PostId): Future[Option[PostData]] = {
    Future {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  override def stop(): Future[Unit] = {
    Future {
      ()
    }
  }
}

