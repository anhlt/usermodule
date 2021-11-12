package db.base
import slick.ast._
import slick.jdbc.PostgresProfile

import java.sql.PreparedStatement
import java.sql.ResultSet
object CustomProfile extends PostgresProfile{
  import java.util.UUID

  override val columnTypes = new JdbcTypes

  class JdbcTypes extends super.JdbcTypes {
    override val uuidJdbcType = new UUIDJdbcType {
      override def sqlTypeName(sym: Option[FieldSymbol]) = "UUID"
      override def valueToSQLLiteral(value: UUID) = "'" + value + "'"
      override def hasLiteralForm = true

      override def setValue(v: UUID, p: PreparedStatement, idx: Int) =
        p.setString(idx, toString(v))
      override def getValue(r: ResultSet, idx: Int) =
        fromString(r.getString(idx))
      override def updateValue(v: UUID, r: ResultSet, idx: Int) =
        r.updateString(idx, toString(v))

      private def toString(uuid: UUID) =
        if (uuid != null) uuid.toString else null
      private def fromString(uuidString: String) =
        if (uuidString != null) UUID.fromString(uuidString) else null
    }
  }
}
