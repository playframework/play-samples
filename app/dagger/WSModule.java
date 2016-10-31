package dagger;

import akka.stream.Materializer;
import play.api.libs.ws.WSClientConfig;
import play.api.libs.ws.WSConfigParser;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.api.libs.ws.ahc.AhcWSClientConfigParser;
import play.libs.ws.WSAPI;
import play.libs.ws.WSClient;

import play.api.*;

import javax.inject.Singleton;

@Module
public class WSModule {

    @Singleton
    @Provides
    WSClientConfig wsClientConfig(Configuration configuration,
                                  Environment environment) {
        return new WSConfigParser(configuration, environment).parse();
    }

    @Singleton
    @Provides
    AhcWSClientConfig ahcWsClientConfig(WSClientConfig wsClientConfig,
                                        Configuration configuration,
                                        Environment environment) {
        return new AhcWSClientConfigParser(wsClientConfig, configuration, environment).parse();
    }

    @Singleton
    @Provides
    WSAPI wsApi(AhcWSClientConfig ahcWSClientConfig, play.inject.ApplicationLifecycle applicationLifecycle, Materializer materializer) {
        return new play.libs.ws.ahc.AhcWSAPI(ahcWSClientConfig, applicationLifecycle, materializer);
    }

    @Singleton
    @Provides
    public WSClient providesWsClient(WSAPI wsapi) {
        return wsapi.client();
    }
}
