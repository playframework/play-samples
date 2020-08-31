package modules;

import akka.actor.typed.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.typed.*;
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
        bind(new TypeLiteral<ActorRef<Command>>() {})
                .toProvider(HelloActorProvider.class)
                .asEagerSingleton();
    }

    public static class HelloActorProvider implements Provider<ActorRef<Command>> {
        private final akka.actor.typed.ActorSystem<Void> actorSystem;
        private Environment environment;

        @Inject
        public HelloActorProvider(ActorSystem actorSystem, Environment environment) {
            this.actorSystem = Adapter.toTyped(actorSystem);
            this.environment = environment;
        }

        @Override
        public ActorRef<Command> get() {

            Cluster cluster = Cluster.get(actorSystem);

            if(environment.isDev()){
                // in Dev Mode, we want a single-node cluster so we join ourself.
                cluster.manager().tell(new Join(cluster.selfMember().address()));
            }else{
                // TODO: use Akka Cluster Bootstrap or some of the other methods to
                // form a cluster (see https://doc.akka.io/docs/akka/current/typed/cluster.html#joining)
            }

            // Initialize the ClusterSingleton Akka extension
            ClusterSingleton clusterSingleton = ClusterSingleton.get(actorSystem);

            SingletonActor<Command> singletonActor = SingletonActor.of(CounterActor.create(), "counter-actor" );
            // Use the Cluster Singleton extension to get an ActorRef to
            // the Counter Actor
            return clusterSingleton.init(singletonActor);
        }
    }
}

