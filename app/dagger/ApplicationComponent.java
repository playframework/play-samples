package dagger;

import javax.inject.Singleton;

/**
 * The application component that specifies all the modules backing
 * the injected components.
 */
@Singleton
@Component(modules = {
    ApplicationLoaderContextModule.class,
    MyComponentsFromContextModule.class,
})
public interface ApplicationComponent {
    play.Application application();
}
