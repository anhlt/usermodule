package controllers.socket

import akka.actor.{Actor, ActorRef, Props}

import scala.collection.mutable.ListBuffer

/**
  * LightSocketActor
  */
class IotSocketActor(out: ActorRef) extends Actor {
  val topic: String = "topic"

  override def receive: Receive = {
    case message: String =>
      play.Logger.debug(s"Message: ${message}")
    // out ! message
  }
}

object IotSocketActor {
  var list: ListBuffer[ActorRef] = ListBuffer.empty[ActorRef]
  def props(out: ActorRef): Props = {
    list += out
    Props(new IotSocketActor(out))
  }

}
