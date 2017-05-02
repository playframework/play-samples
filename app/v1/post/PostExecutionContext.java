package v1.post;

import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

/**
 * Custom execution context wired to "post.repository" thread pool
 */
public class PostExecutionContext extends CustomExecutionContext {

    @Inject
    public PostExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "post.repository");
    }
}
