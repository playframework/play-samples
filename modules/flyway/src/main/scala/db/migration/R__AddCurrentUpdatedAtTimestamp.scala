package db.migration

import java.sql.{Connection, Timestamp}

import org.flywaydb.core.api.migration.jdbc.JdbcMigration

/**
 * Adds a repeatable Flyway migration in Scala.
 *
 * See <a href="https://flywaydb.org/documentation/migration/java">Java migrations</a>
 * for more details.
 */
class R__AddCurrentUpdatedAtTimestamp extends JdbcMigration {

  override def migrate(c: Connection): Unit = {
    val statement = c.prepareStatement("UPDATE users SET updated_at = ?")
    try {
      statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()))
      statement.execute()
    } finally {
      statement.close()
    }
  }
}
