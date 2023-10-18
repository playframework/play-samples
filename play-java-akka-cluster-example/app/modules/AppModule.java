package modules;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.cluster.typed.Cluster;
import org.apache.pekko.cluster.typed.ClusterSingleton;
import org.apache.pekko.cluster.typed.Join;
import org.apache.pekko.cluster.typed.SingletonActor;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import play.Environment;
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
        bind(new TypeLiteral<ActorRef<Command>>() {
        })
                .toProvider(HelloActorProvider.class)
                .asEagerSingleton();
    }

    public static class HelloActorProvider implements Provider<ActorRef<Command>> {
        private final org.apache.pekko.actor.typed.ActorSystem<Void> actorSystem;
        private Environment environment;

        @Inject
        public HelloActorProvider(ActorSystem actorSystem, Environment environment) {
            this.actorSystem = Adapter.toTyped(actorSystem);
            this.environment = environment;
        }

        @Override
        public ActorRef<Command> get() {

            Cluster cluster = Cluster.get(actorSystem);

            if (!environment.isProd()) {
                // in Dev Mode and Test Mode we want a single-node cluster so we join ourself.
                cluster.manager().tell(new Join(cluster.selfMember().address()));
            } else {
                // In Prod mode, there's no need to do anything since
                // the default behavior will be to read the seed node list
                // from the configuration.
                // If you prefer use Pekko Cluster Management, then set it up here.
            }

            // Initialize the ClusterSingleton Pekko extension
            ClusterSingleton clusterSingleton = ClusterSingleton.get(actorSystem);

            SingletonActor<Command> singletonActor = SingletonActor.of(CounterActor.create(), "counter-actor");
            // Use the Cluster Singleton extension to get an ActorRef to
            // the Counter Actor
            return clusterSingleton.init(singletonActor);
        }
    }
}

