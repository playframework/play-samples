package modules;

import play.Application;
import play.ApplicationLoader;
import play.DefaultApplication;
import play.api.BuiltInComponentsFromContext;
import play.inject.DelegateInjector;
import play.routing.Router;
import play.routing.RoutingDsl;

import static play.mvc.Controller.ok;

public class MyApplicationLoader implements ApplicationLoader {
    @Override
    public Application load(Context context) {
        Router router = new RoutingDsl()
                .GET("/").routeTo(() ->
                        ok("Hello")
                )
                .build();
        return new MyComponents(context, router).application();
    }
}

class MyComponents {
    private final ApplicationLoader.Context context;
    private final Router router;

    public MyComponents(ApplicationLoader.Context context, Router router) {
        this.context = context;
        this.router = router;
    }

    public Application application() {
        final BuiltInComponentsFromContext components = providesComponents();
        final play.api.Application scalaApplication = components.application();
        final DelegateInjector injector = new DelegateInjector(scalaApplication.injector());
        return new DefaultApplication(scalaApplication, injector);
    }

    private BuiltInComponentsFromContext providesComponents() {
        return new BuiltInComponentsFromContext(context.underlying()) {
            @Override
            public play.api.routing.Router router() {
                return router.asScala();
            }
        };
    }
}
