package modules;

import akka.actor.typed.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import services.CounterActor;
import services.CounterActor.Command;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 *
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<ActorRef<Command>>() {})
                .toProvider(HelloActorProvider.class)
                .asEagerSingleton();
    }

    public static class HelloActorProvider implements Provider<ActorRef<Command>> {
        private final ActorSystem actorSystem;

        @Inject
        public HelloActorProvider(ActorSystem actorSystem) {
            this.actorSystem = actorSystem;
        }

        @Override
        public ActorRef<Command> get() {
            return Adapter.spawn(actorSystem, CounterActor.create(), "counter-actor");
        }
    }
}

