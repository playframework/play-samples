package dagger;

import play.Application;
import play.ApplicationLoader;

public class MyApplicationLoader implements ApplicationLoader {

    @Override
    public Application load(Context context)
    {
        ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationLoaderContextModule(new ApplicationLoaderContextModule(context))
                .build();

        return applicationComponent.application();
    }
}

