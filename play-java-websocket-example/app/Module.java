import actors.*;
import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Adapter;
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
        bindActorFactory(UserActor.class, UserActor.Factory.class);
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
}
