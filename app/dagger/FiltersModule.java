package dagger;

import filters.LoggingFilter;
import play.api.mvc.EssentialFilter;

import java.util.Arrays;
import java.util.List;

@Module
public class FiltersModule {

    @Provides
    public List<EssentialFilter> providesFilters(LoggingFilter loggingFilter) {
        EssentialFilter[] filters = {loggingFilter};
        return Arrays.asList(filters);
    }

}
