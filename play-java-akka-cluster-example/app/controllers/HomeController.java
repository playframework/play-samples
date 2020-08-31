package controllers;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import play.mvc.Controller;
import play.mvc.Result;
import services.CounterActor;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    private ActorRef<CounterActor.Command> counterActor;
    private Scheduler scheduler;

    private Duration askTimeout = Duration.ofSeconds(3L);

    @Inject
    public HomeController(ActorRef<CounterActor.Command> counterActor, Scheduler scheduler) {
        this.counterActor = counterActor;
        this.scheduler = scheduler;
    }


    public CompletionStage<Result> index() {
        return AskPattern.<CounterActor.Command, Integer>ask(
                counterActor,
                CounterActor.GetValue::new,
                askTimeout,
                scheduler)
                .thenApply(this::renderIndex);
    }

    public CompletionStage<Result> increment() {
        return AskPattern.<CounterActor.Command, Integer>ask(
                counterActor,
                CounterActor.Increment::new,
                askTimeout,
                scheduler)
                .thenApply(this::renderIndex);
    }

    private Result renderIndex(Integer hitCounter) {
        return ok(views.html.index.render(hitCounter));
    }

}
