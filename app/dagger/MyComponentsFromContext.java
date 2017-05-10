package dagger;

import controllers.TimeController;
import play.ApplicationLoader;
import play.BuiltInComponentsFromContext;
import play.api.routing.Router;
import play.components.BodyParserComponents;
import play.controllers.AssetsComponents;
import play.core.j.DefaultJavaHandlerComponents;
import play.core.j.JavaHandlerComponents;
import play.data.FormFactoryComponents;
import play.filters.components.NoHttpFiltersComponents;
import play.inject.Injector;
import play.libs.ws.ahc.AhcWSComponents;
import router.Routes;

import javax.inject.Inject;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A components class that contains a clock instance injected from Dagger.
 */
public class MyComponentsFromContext extends BuiltInComponentsFromContext
        implements NoHttpFiltersComponents, AssetsComponents, AhcWSComponents, FormFactoryComponents, BodyParserComponents {

    private final Clock clock;

    @Inject
    public MyComponentsFromContext(ApplicationLoader.Context context, Clock clock) {
        super(context);
        this.clock = clock;
    }

    @Override
    public Injector injector() {
        // This probably should be solved by BuiltInComponentsFromContext itself
        Injector injector = super.injector();

        Map<Class, Supplier<Object>> extraMappings = new HashMap<>();
        SimpleInjector simpleInjector = new SimpleInjector(injector, extraMappings);

        extraMappings.put(JavaHandlerComponents.class, () -> new DefaultJavaHandlerComponents(simpleInjector.asScala(), actionCreator(), httpConfiguration(), executionContext(), javaContextComponents()));
        extraMappings.put(play.mvc.BodyParser.Default.class, this::defaultParser);

        return simpleInjector;
    }

    private TimeController timeController(){
        return new controllers.TimeController(clock, wsClient(), formFactory());
    }

    @Override
    public play.routing.Router router() {
        Router routes = new Routes(scalaHttpErrorHandler(), timeController(), assets());
        return routes.asJava();
    }
}