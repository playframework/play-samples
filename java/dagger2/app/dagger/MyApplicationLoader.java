package dagger;

import play.Application;
import play.ApplicationLoader;
import play.LoggerConfigurator;

import java.util.Optional;

import static java.util.Collections.*;

/**
 * This class loads an application through Dagger compile time dependency injection.
 */
public class MyApplicationLoader implements ApplicationLoader {

    @Override
    public Application load(Context context)
    {
        final ClassLoader classLoader = context.environment().classLoader();
        final Optional<LoggerConfigurator> opt = LoggerConfigurator.apply(classLoader);
        opt.ifPresent(lc -> lc.configure(context.environment(), context.initialConfig(), emptyMap()));

        ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .context(context)
                .build();

        return applicationComponent.application();
    }
}

