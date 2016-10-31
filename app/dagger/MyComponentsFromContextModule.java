package dagger;

import play.DefaultApplication;

import javax.inject.Singleton;

@Module
public class MyComponentsFromContextModule {

    @Singleton
    @Provides
    public play.api.Application scalaApplication(MyComponentsFromContext myComponentsFromContext) {
        return myComponentsFromContext.application();
    }

    @Singleton
    @Provides
    public play.Application javaApplication(play.api.Application app, play.inject.Injector injector) {
        return new DefaultApplication(app, injector);
    }

    @Singleton
    @Provides
    public play.api.http.HttpErrorHandler httpErrorHandler(MyComponentsFromContext myComponentsFromContext) {
        return myComponentsFromContext.httpErrorHandler();
    }

    @Singleton
    @Provides
    public play.api.inject.Injector providesInjector(MyComponentsFromContext myComponentsFromContext) {
        return myComponentsFromContext.injector();
    }

    @Singleton
    @Provides
    public play.inject.Injector providesJavaInjector(play.api.inject.Injector injector) {
        return new play.inject.DelegateInjector(injector);
    }
}
