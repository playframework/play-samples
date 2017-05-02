package dagger;

import play.Application;

@Module
public class MyComponentsFromContextModule {

    @Provides
    public Application providesApplication(MyComponentsFromContext myComponentsFromContext) {
        return myComponentsFromContext.application();
    }

}