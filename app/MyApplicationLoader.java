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
        // XXX Annotation based Java Actions don't work in 2.6.x snapshot...
        return new RoutingDsl()
                .GET("/").routeTo(() ->
                        ok("Hello")
                ).build().asScala();
    }

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
