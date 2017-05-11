import play.filters.csrf.CSRFFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Add the CSRF filter for form processing.
 */
@Singleton
public class Filters extends DefaultHttpFilters {

    @Inject
    public Filters(CSRFFilter csrfFilter) {
        super(csrfFilter);
    }
}
