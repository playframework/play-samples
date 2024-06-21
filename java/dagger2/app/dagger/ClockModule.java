package dagger;

import java.time.Clock;

/**
 * A module that provides a clock implementation.
 */
@Module
public abstract class ClockModule {

    @Provides
    public static Clock clock() {
        return java.time.Clock.systemUTC();
    }

}
