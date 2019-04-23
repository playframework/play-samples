package dagger;

@Module
public abstract class ApplicationLoaderContextModule {

    @Provides
    public static play.api.ApplicationLoader.Context providesScalaContext(play.ApplicationLoader.Context context) {
        return context.asScala();
    }

}
