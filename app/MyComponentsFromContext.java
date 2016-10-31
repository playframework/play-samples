import dagger.Component;
import dagger.Module;
import dagger.Provides;
import play.ApplicationLoader;
import play.DefaultApplication;
import play.api.routing.Router;
import play.inject.DelegateInjector;
import play.routing.RoutingDsl;

import javax.inject.Singleton;

import static play.mvc.Results.ok;

class MyComponentsFromContext extends play.api.BuiltInComponentsFromContext {

    private final MyComponents components;

    public MyComponentsFromContext(play.api.ApplicationLoader.Context context) {
        super(context);
        final ApplicationLoader.Context javaContext = new ApplicationLoader.Context(context);
        this.components = DaggerMyComponents.builder()
                .applicationModule(new ApplicationModule(javaContext))
                .build();
    }

    @Override
    public play.api.routing.Router router() {
        return components.router();
    }

    public play.Application javaApplication() {
        final play.api.Application scalaApp = this.application();
        final DelegateInjector injector = new DelegateInjector(scalaApp.injector());
        return new DefaultApplication(scalaApp, injector);
    }
}


@Singleton
@Component(modules = { ApplicationModule.class })
interface MyComponents {
    Router router();
}

@Module
class ApplicationModule {

    private final play.ApplicationLoader.Context context;

    public ApplicationModule(play.ApplicationLoader.Context context) {
        this.context = context;
    }

    @Provides
    play.ApplicationLoader.Context providesContext() {
        return this.context;
    }

    @Provides
    @Singleton
    Router providesRouter() {
        return new RoutingDsl()
                .GET("/").routeTo(() -> ok("Hello"))
                .build().asScala();
    }

}
