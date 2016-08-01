package post

import javax.inject.{Inject, Provider, Singleton}

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.lightbend.blog.comment._
import com.lightbend.blog.post._
import play.api.inject.ApplicationLifecycle

class PostModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CommentRepository]).toProvider(classOf[CommentRepositoryProvider])
    bind(classOf[PostRepository]).toProvider(classOf[PostRepositoryProvider])
  }
}

/**
 * Provides a post repository for Play.
 */
@Singleton
class PostRepositoryProvider @Inject()(applicationLifecycle: ApplicationLifecycle,
                                       actorSystem: ActorSystem)
  extends Provider[PostRepository] {

  lazy val get: PostRepository = {
    val repo = new PostRepositoryImpl(executionContext)
    // Hooks the repository lifecycle to Play's lifecycle, so any resources are shutdown
    applicationLifecycle.addStopHook { () =>
      repo.stop()
    }
    repo
  }

  private def executionContext: PostExecutionContext = {
    //val ec = actorSystem.dispatchers.lookup("post.dispatcher")
    val ec = actorSystem.dispatchers.defaultGlobalDispatcher
    new PostExecutionContext(ec)
  }
}

/**
 * Provides a comment repository for Play.
 */
@Singleton
class CommentRepositoryProvider @Inject()(applicationLifecycle: ApplicationLifecycle,
                                          actorSystem: ActorSystem)
  extends Provider[CommentRepository] {

  lazy val get: CommentRepository = {
    val repo = new CommentRepositoryImpl(executionContext)
    // Hooks the repository lifecycle to Play's lifecycle, so any resources are shutdown
    applicationLifecycle.addStopHook { () =>
      repo.stop()
    }
    repo
  }

  private def executionContext: CommentExecutionContext = {
    //val ec = actorSystem.dispatchers.lookup("post.dispatcher")
    val ec = actorSystem.dispatchers.defaultGlobalDispatcher
    new CommentExecutionContext(ec)
  }

}
