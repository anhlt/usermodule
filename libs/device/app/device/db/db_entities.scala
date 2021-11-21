package device.db

import org.joda.time.DateTime
import db.base.Entity
import java.util.UUID

case class DBDeviceModel(
    id: UUID,
    desc: String,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBDeviceInstance(
    id: UUID,
    model: UUID,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBDeviceOwner(
    id: UUID,
    userID: UUID,
    deviceInstanceID: UUID,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity
