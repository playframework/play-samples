package dagger;

import javax.inject.Singleton;

/**
 * The application component that specifies all the modules backing
 * the injected components.
 */
@Singleton
@Component(modules = {
        ActorSystemModule.class,
        AssetsModule.class,
        ApplicationLoaderContextModule.class,
        ClockModule.class,
        FiltersModule.class,
        JavaFormsModule.class,
        MessagesApiModule.class,
        MyComponentsFromContextModule.class,
        RouterModule.class,
        WSModule.class
})
public interface ApplicationComponent {
    play.Application application();
}
