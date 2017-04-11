package dagger;

import controllers.AssetsConfiguration;
import controllers.AssetsMetadata;
import controllers.AssetsMetadataProvider;
import controllers.DefaultAssetsMetadata;
import play.api.Configuration;
import play.api.Environment;
import play.api.http.FileMimeTypes;
import play.api.inject.ApplicationLifecycle;

import javax.inject.Provider;
import javax.inject.Singleton;

/**
 *
 */
@Module
public class AssetsModule {


    @Singleton
    @Provides
    public AssetsConfiguration assetsConfiguration(Configuration configuration, Environment environment) {
        return AssetsConfiguration.fromConfiguration(configuration, environment.mode());
    }

    @Singleton
    @Provides
    public AssetsMetadata providesAssetsMetadata(Environment env, AssetsConfiguration config, FileMimeTypes fileMimeTypes, ApplicationLifecycle lifecycle) {
        Provider<DefaultAssetsMetadata> provider = new AssetsMetadataProvider(env, config, fileMimeTypes, lifecycle);
        return provider.get();
    }

}
