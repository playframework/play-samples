import javax.inject.Inject

import filters.StrictTransportSecurityFilter
import play.api.http.DefaultHttpFilters
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter
import play.filters.hosts.AllowedHostsFilter

/**
 * https://www.playframework.com/documentation/latest/ScalaHttpFilters
 */
class Filters @Inject()(hstsFilter: StrictTransportSecurityFilter,
                        csrfFilter: CSRFFilter,
                        securityFilter: SecurityHeadersFilter,
                        allowedHostsFilter: AllowedHostsFilter)
  extends DefaultHttpFilters(hstsFilter, csrfFilter, securityFilter, allowedHostsFilter)
