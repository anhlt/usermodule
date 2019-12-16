package utils.filters

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.api.http.EnabledFilters
import play.filters.gzip.GzipFilter
import play.filters.csrf.CSRFFilter

class MyFilters @Inject()(
    defaultFilters: EnabledFilters,
    gzip: GzipFilter,
    log: LoggingFilter
) extends DefaultHttpFilters(defaultFilters.filters :+ gzip :+ log: _*)
