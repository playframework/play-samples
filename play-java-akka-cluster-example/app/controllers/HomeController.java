package controllers;

import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.Scheduler;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.actor.typed.javadsl.AskPattern;
import org.apache.pekko.cluster.typed.Cluster;
import org.apache.pekko.cluster.typed.ClusterSingleton;
import org.apache.pekko.cluster.typed.SingletonActor;
import play.mvc.Controller;
import play.mvc.Result;
import services.CounterActor;
import services.CounterActor.Command;
import services.CounterActor.GetValue;
import services.CounterActor.Increment;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    private ActorRef<Command> counterActor;
    private Scheduler scheduler;

    private Duration askTimeout = Duration.ofSeconds(3L);

    @Inject
    public HomeController(ActorRef<CounterActor.Command> counterActor, Scheduler scheduler) {
        this.counterActor = counterActor;
        this.scheduler = scheduler;
    }


    public CompletionStage<Result> index() {
        // https://www.playframework.com/documentation/latest/PekkoTyped#Using-the-AskPattern-&-Typed-Scheduler
        return AskPattern.<Command, Integer>ask(
                counterActor,
                GetValue::new,
                askTimeout,
                scheduler)
                .thenApply(this::renderIndex);
    }

    public CompletionStage<Result> increment() {
        // https://www.playframework.com/documentation/latest/PekkoTyped#Using-the-AskPattern-&-Typed-Scheduler
        return AskPattern.<Command, Integer>ask(
                counterActor,
                Increment::new,
                askTimeout,
                scheduler)
                .thenApply(this::renderIndex);
    }

    private Result renderIndex(Integer hitCounter) {
        return ok(views.html.index.render(hitCounter));
    }

}
