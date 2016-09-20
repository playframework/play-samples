package v1.post

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

final case class PostData(id: PostId, title: String, body: String)

class PostId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object PostId {
  def apply(raw: String): PostId = {
    require(raw != null)
    new PostId(Integer.parseInt(raw))
  }
}

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait PostRepository {
  def create(data: PostData): Future[PostId]

  def list(): Future[Iterable[PostData]]

  def get(id: PostId): Future[Option[PostData]]
}

/**
  * A trivial implementation for the Post Repository.
  */
@Singleton
class PostRepositoryImpl @Inject() extends PostRepository {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private val postList = List(
    PostData(PostId("1"), "title 1", "blog post 1"),
    PostData(PostId("2"), "title 2", "blog post 2"),
    PostData(PostId("3"), "title 3", "blog post 3"),
    PostData(PostId("4"), "title 4", "blog post 4"),
    PostData(PostId("5"), "title 5", "blog post 5")
  )

  override def list(): Future[Iterable[PostData]] = {
    Future.successful {
      logger.trace(s"list: ")
      postList
    }
  }

  override def get(id: PostId): Future[Option[PostData]] = {
    Future.successful {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  def create(data: PostData): Future[PostId] = {
    Future.successful {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
