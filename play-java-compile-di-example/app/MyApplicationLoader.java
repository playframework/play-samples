import play.Application;
import play.ApplicationLoader;
import play.BuiltInComponentsFromContext;
import play.LoggerConfigurator;
import play.filters.components.HttpFiltersComponents;
import play.routing.RoutingDsl;
import play.routing.RoutingDslComponents;

import java.util.Collections;

import static play.mvc.Results.ok;

public class MyApplicationLoader implements ApplicationLoader {
    @Override
    public Application load(Context context) {
        LoggerConfigurator.apply(context.environment().classLoader()).ifPresent(lc ->
            lc.configure(context.environment(), context.initialConfig(), Collections.emptyMap())
        );

        return new MyComponents(context).application();
    }
}

class MyComponents extends BuiltInComponentsFromContext implements HttpFiltersComponents, RoutingDslComponents {

    public MyComponents(ApplicationLoader.Context context) {
         super(context);
    }

    @Override
    public play.routing.Router router() {
        RoutingDsl routingDsl = routingDsl();
        return routingDsl.GET("/").routingTo(_request ->
                        ok("Hello")
                ).build();
    }

}
