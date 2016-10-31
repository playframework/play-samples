# play-java-dagger2

Play Application using Dagger 2 for Compile Time DI

This project shows how to use Play Java with [Dagger 2](https://google.github.io/dagger/).

## Running

```
sbt run
```

Then go to http://localhost:9000 to see the time.

Go to http://localhost:9000/tz to select and change the time zone.

## Special Note on Form Handling

There are some components which must be delayed until the last possible moment, especially components that depend on an `Injector`.

This means if you are using forms in your components, you must declare `Lazy<FormFactory>` so that the controller does not resolve the `FormFactory` from the router before the application has finished loading.

## Background

Dagger 2 is a compile time dependency injection system, so the graph must be carefully thought out and everything must be available using @Provides annotations.  

This means that out of the box Play components like play.api.inject.BuiltinModule don't quite work, because they are sequences of classes, and don't have the static API signature that Dagger expects.

The key is creating lots of small `Module` that have `@Provides` methods off the injected component so that Dagger can do the delegation:

```
@Module
class FooModule {

  private final Foo foo;

  @Inject
  public FooModule(Foo Foo) {
    this.foo = foo;
  }
  
  @Provides
  public Bar providesBar() {
    return foo.bar();
  }
}
```

Then, you load all the modules up in the `ApplicationComponent` `@Component` annotation.  There's [Mortor](https://github.com/square/mortar) if you want something more advanced.
