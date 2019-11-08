import actors.*;
import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Adapter;
import akka.stream.Materializer;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import play.libs.akka.AkkaGuiceSupport;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@SuppressWarnings("unused")
public class Module extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ActorRef<StocksActor.GetStocks>>() {})
            .toProvider(StocksActorProvider.class)
            .asEagerSingleton();
        bindActor(UserParentActor.class, "userParentActor");
        bind(UserActor.Factory.class).toProvider(UserActorFactoryProvider.class);
    }

    @Singleton
    public static class StocksActorProvider implements Provider<ActorRef<StocksActor.GetStocks>> {
        private final ActorSystem actorSystem;

        @Inject
        public StocksActorProvider(ActorSystem actorSystem) {
            this.actorSystem = actorSystem;
        }

        @Override
        public ActorRef<StocksActor.GetStocks> get() {
            return Adapter.spawn(
                actorSystem,
                StocksActor.create(),
                "stocksActor");
        }
    }

    @Singleton
    public static class UserActorFactoryProvider implements Provider<UserActor.Factory> {
        private final ActorRef<StocksActor.GetStocks> stocksActor;
        private final Materializer mat;

        @Inject
        public UserActorFactoryProvider(ActorRef<StocksActor.GetStocks> stocksActor, Materializer mat) {
            this.stocksActor = stocksActor;
            this.mat = mat;
        }

        @Override
        public UserActor.Factory get() {
            return id -> UserActor.create(id, stocksActor, mat);
        }
    }
}
