package dagger;

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
    public play.api.ApplicationLoader.Context providesScalaContext() {
        return this.javaContext.asScala();
    }

    @Singleton
    @Provides
    public play.ApplicationLoader.Context providesJavaContext() {
        return javaContext;
    }

}
