import play.filters.csrf.CSRFFilter;
import play.filters.headers.SecurityHeadersFilter;
import play.filters.hosts.AllowedHostsFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class configures filters that run on every request. This
 * class is queried by Play to get a list of filters.
 *
 * https://www.playframework.com/documentation/latest/ScalaCsrf
 * https://www.playframework.com/documentation/latest/AllowedHostsFilter
 * https://www.playframework.com/documentation/latest/SecurityHeaders
 */
@Singleton
public class Filters extends DefaultHttpFilters {

    @Inject
    public Filters(CSRFFilter csrfFilter,
                   AllowedHostsFilter allowedHostsFilter,
                   SecurityHeadersFilter securityHeadersFilter) {
        super(csrfFilter, allowedHostsFilter, securityHeadersFilter);
    }
}
