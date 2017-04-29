package dagger;

import java.time.Clock;

/**
 * A module that provides a clock implementation.
 */
@Module
public class ClockModule {

    @Provides
    public Clock clock() {
        return java.time.Clock.systemUTC();
    }

}
