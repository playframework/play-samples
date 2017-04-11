package dagger;

import controllers.AssetsConfiguration;
import play.api.Configuration;
import play.api.Environment;
import play.api.http.DefaultFileMimeTypesProvider;
import play.api.http.FileMimeTypes;
import play.api.http.FileMimeTypesConfiguration;
import play.api.http.HttpConfiguration;
import play.api.inject.ApplicationLifecycle;
import play.core.SourceMapper;
import play.core.WebCommands;
import play.inject.DelegateApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Module
public class ApplicationLoaderContextModule {

    private final play.ApplicationLoader.Context javaContext;

    @Inject
    public ApplicationLoaderContextModule(play.ApplicationLoader.Context context){
        this.javaContext = Objects.requireNonNull(context);
    }

    @Singleton
    @Provides
    public play.api.http.HttpConfiguration httpConfiguration(Configuration configuration, Environment environment) {
        return HttpConfiguration.fromConfiguration(configuration, environment);
    }

    @Singleton
    @Provides
    public FileMimeTypes fileMimeTypes(HttpConfiguration config) {
        return new DefaultFileMimeTypesProvider(config.fileMimeTypes()).get();
    }

    public play.api.ApplicationLoader.Context context() {
        return this.javaContext.underlying();
    }

    @Singleton
    @Provides
    public Environment environment() {
        return context().environment();
    }

    @Singleton
    @Provides
    public Configuration configuration() {
        return context().initialConfiguration();
    }

    @Singleton
    @Provides
    public ApplicationLifecycle applicationLifecycle() {
        return context().lifecycle();
    }

    @Singleton
    @Provides
    public scala.Option<SourceMapper> sourceMapper() { return context().sourceMapper(); }

    @Singleton
    @Provides
    public WebCommands webCommands() {
        return context().webCommands();
    }

    @Singleton
    @Provides
    public play.inject.ApplicationLifecycle providesJavaApplicationLifecycle(ApplicationLifecycle lifecycle) {
        return new DelegateApplicationLifecycle(lifecycle);
    }

    @Singleton
    @Provides
    public play.ApplicationLoader.Context providesJavaContext() {
        return javaContext;
    }

    @Singleton
    @Provides
    public play.Configuration providesJavaConfiguration() {
        return new play.Configuration(configuration());
    }

}
