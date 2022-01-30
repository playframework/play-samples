include "application.conf"

// Remove DB_CLOSE_DELAY=-1 as it will cause the database to persist between tests. However we want the database to
// persist between application loads when we are running the application normally.
slick.dbs.default.db.url="jdbc:h2:mem:play"
