package dagger;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.Materializer;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.ApplicationLifecycle;
import play.api.libs.concurrent.ActorSystemProvider;

import javax.inject.Singleton;

@Module
public class ActorSystemModule {

    @Singleton
    @Provides
    public ActorSystem providesActorSystem(Environment env, Configuration conf, ApplicationLifecycle lifecycle) {
        return new ActorSystemProvider(env, conf, lifecycle).get();
    }

    @Singleton
    @Provides
    public Materializer providesMaterializer(ActorSystem actorSystem) {
        final ActorMaterializerSettings settings = ActorMaterializerSettings.create(actorSystem);
        return ActorMaterializer.create(settings, actorSystem);
    }
}
