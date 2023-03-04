# play-java-dagger2-example

This project shows how to use Play Java with [Dagger 2](https://google.github.io/dagger/).

## Running

```
sbt run
```

Then go to http://localhost:9000 to see the time and change the time zone.

Go to http://localhost:9000/ws to see the WS client pull the time from a remote service (really the app itself). 

## Background

[Dagger 2](https://google.github.io/dagger/) is a compile time dependency injection system. This means that [dependencies are still declared](https://google.github.io/dagger/users-guide.html#declaring-dependencies) with `@Inject`, but the compiler is responsible for resolving the graph.

Play Java supports [Compile Time Dependency Injection](https://www.playframework.com/documentation/latest/JavaCompileTimeDependencyInjection) so the work here is to provide an application loader that hooks into Dagger, rather than using constructor based DI.

The `dagger.MyApplicationLoader` class provides the core, by calling out to the `DaggerApplicationComponent`:

```java
public class dagger.MyApplicationLoader implements ApplicationLoader {

    @Override
    public Application load(Context context)
    {
        final ClassLoader classLoader = context.environment().classLoader();
        final Optional<LoggerConfigurator> opt = LoggerConfigurator.apply(classLoader);
        opt.ifPresent(lc -> lc.configure(context.environment(), context.initialConfig(), emptyMap()));

        ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationLoaderContextModule(new ApplicationLoaderContextModule(context))
                .build();

        return applicationComponent.application();
    }
}
```

From there, it's a question of providing components by extending `BuiltInComponentsFromContext`.  The `ClockModule` is included to show that you can provide your own custom modules to Dagger.
 
```java
public class MyComponentsFromContext extends BuiltInComponentsFromContext
        implements NoHttpFiltersComponents, AssetsComponents, AhcWSComponents, FormFactoryComponents, BodyParserComponents {

    private final Clock clock;

    @Inject
    public MyComponentsFromContext(ApplicationLoader.Context context, Clock clock) {
        super(context);
        this.clock = clock;
    }

    private TimeController timeController() {
        return new controllers.TimeController(clock, wsClient(), formFactory());
    }

    @Override
    public play.routing.Router router() {
        Router routes = new Routes(scalaHttpErrorHandler(), timeController(), assets());
        return routes.asJava();
    }
}
```

## SimpleInjector
 
There is a small amount of extra configuration, because the Java annotation system still requires a small amount of runtime dependency injection -- this is fixed by putting a couple of extra mappings into a delegating injector.

```java
public class MyComponentsFromContext {
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
}
```
