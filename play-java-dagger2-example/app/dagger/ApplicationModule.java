package dagger;

import play.Application;

@Module
public abstract class ApplicationModule {

    @Provides
    public static Application providesApplication(MyComponentsFromContext myComponentsFromContext) {
        return myComponentsFromContext.application();
    }

}