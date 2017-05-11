package dagger;

import play.Application;

@Module
public class ApplicationModule {

    @Provides
    public Application providesApplication(MyComponentsFromContext myComponentsFromContext) {
        return myComponentsFromContext.application();
    }

}