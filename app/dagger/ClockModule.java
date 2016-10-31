package dagger;

import javax.inject.Singleton;
import java.time.Clock;

@Module
public class ClockModule {

    @Singleton
    @Provides
    public Clock providesClock() {
        return java.time.Clock.systemUTC();
    }
}
