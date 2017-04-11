package dagger;

import play.api.Environment;
import play.api.http.HttpConfiguration;
import play.api.i18n.DefaultLangsProvider;
import play.api.i18n.DefaultMessagesApiProvider;

import javax.inject.Singleton;

@Module
public class MessagesApiModule {

    @Singleton
    @Provides
    public play.i18n.MessagesApi providesJavaMessagesApi(play.api.i18n.MessagesApi messagesApi) {
        return new play.i18n.MessagesApi(messagesApi);
    }

    @Singleton
    @Provides
    public play.i18n.Langs providesJavaLangs(play.api.i18n.Langs langs) {
        return new play.i18n.Langs(langs);
    }

    @Singleton
    @Provides
    public play.api.i18n.MessagesApi providesMessagesApi(Environment env, play.api.Configuration config, play.api.i18n.Langs langs, HttpConfiguration httpConfiguration) {
        return new DefaultMessagesApiProvider(env, config, langs, httpConfiguration).get();
    }

    @Singleton
    @Provides
    public play.api.i18n.Langs providesScalaLangs(play.api.Configuration config) {
        return new DefaultLangsProvider(config).get();
    }
}
