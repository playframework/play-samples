package dagger;

import play.Application;

import javax.inject.Singleton;

@Module
public class MyComponentsFromContextModule {

    @Singleton
    @Provides
    public Application providesInjector(MyComponentsFromContext myComponentsFromContext) {
        return myComponentsFromContext.application();
    }

}