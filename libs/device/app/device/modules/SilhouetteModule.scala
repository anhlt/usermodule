package device.modules

import com.google.inject._
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import utils.auth.DefaultEnv

class SilhouetteModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[EventBus]).toInstance(EventBus())
    bind(classOf[Clock]).toInstance(Clock())
  }



  @Provides
  @Named("RedToothPick")
  def provideSilhouette(
      env: Environment[DefaultEnv],
      securedAction: SecuredAction,
      unsecuredAction: UnsecuredAction,
      userAwareAction: UserAwareAction
  ): Silhouette[DefaultEnv] = {
    new SilhouetteProvider[DefaultEnv](
      env,
      securedAction,
      unsecuredAction,
      userAwareAction
    )
  }

}
