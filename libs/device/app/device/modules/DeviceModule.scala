package device.modules

import com.google.inject._
import com.google.inject.name.Named
import device.db.TableDefinitions
import device.models.repository.DeviceRepository
import device.models.repository.DeviceRepositoryImpl

class DeviceModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[TableDefinitions])
    bind(classOf[DeviceRepository]).to(classOf[DeviceRepositoryImpl])

  }

}
