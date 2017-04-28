package dagger;

import controllers.TimeController;
import play.ApplicationLoader;
import play.BuiltInComponentsFromContext;
import play.api.routing.Router;
import play.controllers.AssetsComponents;
import play.data.FormFactoryComponents;
import play.filters.components.HttpFiltersComponents;
import play.libs.ws.ahc.AhcWSComponents;
import router.Routes;

import javax.inject.Inject;

public class MyComponentsFromContext extends BuiltInComponentsFromContext
        implements HttpFiltersComponents, AssetsComponents, AhcWSComponents, FormFactoryComponents {

    @Inject
    public MyComponentsFromContext(ApplicationLoader.Context context) {
        super(context);
    }

    private TimeController timeController(){
        return new controllers.TimeController(java.time.Clock.systemUTC(), wsClient(), formFactory());
    }

    @Override
    public play.routing.Router router() {
        Router routes = new Routes(scalaHttpErrorHandler(), timeController(), assets());
        return routes.asJava();
    }
}