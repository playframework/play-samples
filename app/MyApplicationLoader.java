import org.slf4j.ILoggerFactory;
import play.Application;
import play.ApplicationLoader;
import play.DefaultApplication;
import play.Environment;
import play.api.LoggerConfigurator$;
import play.inject.DelegateInjector;
import play.routing.RoutingDsl;
import scala.compat.java8.OptionConverters;

import java.util.Optional;

import static play.mvc.Results.ok;

public class MyApplicationLoader implements ApplicationLoader {
    @Override
    public Application load(Context context) {
        final ClassLoader classLoader = context.environment().classLoader();
        final Optional<LoggerConfigurator> opt = LoggerConfigurator.fromClassLoader(classLoader);
        opt.ifPresent(lc -> lc.configure(context.environment()));

        final play.api.Application scalaApp = new MyComponents(context.underlying()).application();
        final DelegateInjector injector = new DelegateInjector(scalaApp.injector());
        return new DefaultApplication(scalaApp, injector);
    }

}

class MyComponents extends play.api.BuiltInComponentsFromContext {

    public MyComponents(play.api.ApplicationLoader.Context context) {
        super(context);
    }

    @Override
    public play.api.routing.Router router() {
        return new RoutingDsl()
                .GET("/").routeTo(() ->
                        ok("Hello")
                ).build().asScala();
    }

    // Annotation based Java Actions don't work straight out of the box.
    // To use a generated routes file with Java, you need to wrap the controller
    // in a handler invoker...
    /*
    class Routes(
      override val errorHandler: play.api.http.HttpErrorHandler,
      HomeController_0: controllers.HomeController,
      val prefix: String
    ) extends GeneratedRouter {

      private[this] lazy val controllers_HomeController_index0_invoker = createInvoker(
        HomeController_0.index,
        HandlerDef(this.getClass.getClassLoader,
          "router",
          "controllers.HomeController",
          "index",
          Nil,
          "GET",
          """ An example controller showing a sample home page""",
          this.prefix + """"""
        )
      )

    def routes: PartialFunction[RequestHeader, Handler] = {
      case controllers_HomeController_index0_route(params) =>
        call {
          controllers_HomeController_index0_invoker.call(HomeController_0.index)
        }
      }
     */
}

class LoggerConfigurator {

    private final play.api.LoggerConfigurator delegate;

    public LoggerConfigurator(play.api.LoggerConfigurator delegate) {
        this.delegate = delegate;
    }

    public static Optional<LoggerConfigurator> fromClassLoader(ClassLoader classLoader) {
        return OptionConverters.toJava(LoggerConfigurator$.MODULE$.apply(classLoader)).map(LoggerConfigurator::new);
    }

    public void configure(Environment env) {
        delegate.configure(env.underlying());
    }

}
