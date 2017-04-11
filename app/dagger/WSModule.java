package dagger;

import akka.stream.Materializer;
import play.api.Configuration;
import play.api.Environment;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.api.libs.ws.ahc.cache.EffectiveURIKey;
import play.api.libs.ws.ahc.cache.ResponseEntry;
import play.libs.ws.WSClient;
import play.libs.ws.ahc.AhcWSClient;
import play.libs.ws.ahc.AhcWSClientConfigFactory;
import play.libs.ws.ahc.StandaloneAhcWSClient;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.inject.Singleton;

@Module
public class WSModule {

    @Singleton
    @Provides
    public WSClient providesWsClient(Configuration configuration, Environment environment, Materializer materializer) {
        final AhcWSClientConfig config = AhcWSClientConfigFactory.forConfig(configuration.underlying(), environment.classLoader());
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

        Cache<EffectiveURIKey, ResponseEntry> cache = cacheManager.createCache("play-ws-cache",
                new MutableConfiguration()
                        .setTypes(EffectiveURIKey.class, ResponseEntry.class)
                        .setStoreByValue(false)
                        .setExpiryPolicyFactory(new FactoryBuilder.SingletonFactory<>(new EternalExpiryPolicy())));

        final StandaloneAhcWSClient client = StandaloneAhcWSClient.create(config, cache, materializer);
        return new AhcWSClient(client);
    }
}
