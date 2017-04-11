[<img src="https://img.shields.io/travis/playframework/play-java-dagger2-example.svg"/>](https://travis-ci.org/playframework/play-java-dagger2-example)

# play-java-dagger2-example

This project shows how to use Play Java with [Dagger 2](https://google.github.io/dagger/).

## Running

```
sbt run
```

Then go to http://localhost:9000 to see the time and change the time zone.

Go to http://localhost:9000/ws to see the WS client pull the time from a remote service. 

## Background

[Dagger 2](https://google.github.io/dagger/) is a compile time dependency injection system.

This means that [dependencies are still declared](https://google.github.io/dagger/users-guide.html#declaring-dependencies) with `@Inject`, but the compiler is responsible for resolving the graph.  This means the graph must be carefully thought out and everything must be available using @Provides annotations.

Out of the box Play components like play.api.inject.BuiltinModule don't quite work, because they are sequences of classes, and don't have the static API signature that Dagger expects.

The key is creating lots of small `Module` that have `@Provides` methods off the injected component so that Dagger can do the delegation:

```
@Module
class FooModule {

  private final Foo foo;

  @Inject
  public FooModule(Foo Foo) {
    this.foo = foo;
  }
  
  @Singleton
  @Provides
  public Bar providesBar() {
    return foo.bar();
  }
}
```

Then, you match all the `@Provides` and `@Inject` dependencies in the `ApplicationComponent` `@Component` annotation.  

## Special Note on Form Handling

There are some components which must be delayed until the last possible moment, especially components that depend on an `Injector`.

This means if you are using forms in your components, you must declare `Lazy<FormFactory>` so that the controller does not resolve the `FormFactory` from the router before the application has finished loading.
