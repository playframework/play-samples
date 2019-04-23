package dagger;

import javax.inject.Singleton;

/**
 * The application component that specifies all the modules backing
 * the injected components.
 */
@Singleton
@Component(modules = {
    ApplicationLoaderContextModule.class,
    ApplicationModule.class,
    ClockModule.class
})
public interface ApplicationComponent {
    play.Application application();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder context(play.ApplicationLoader.Context context);
        ApplicationComponent build();
    }
}
