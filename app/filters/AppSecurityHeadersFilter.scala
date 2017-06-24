package filters

import javax.inject.Inject

import play.api.http.DefaultHttpFilters
import play.filters.headers.SecurityHeadersFilter

class AppSecurityHeadersFilter @Inject() (securityHeadersFilter: SecurityHeadersFilter) extends DefaultHttpFilters(securityHeadersFilter)