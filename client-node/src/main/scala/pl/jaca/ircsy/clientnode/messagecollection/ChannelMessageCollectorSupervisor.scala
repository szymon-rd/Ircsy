package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import pl.jaca.ircsy.chat.ServerDesc
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollectorSupervisor.Initialize
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-15 at 11
  */
class ChannelMessageCollectorSupervisor extends Actor with ActorLogging {

  log.debug("Starting channel message collector supervisor.")

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 20, withinTimeRange = 1 hour, loggingEnabled = true) {
      case _: ActorInitializationException => Stop
      case _: Exception => Restart
    }

  override def receive: Receive = uninitialized

  def uninitialized: Receive = {
    case Initialize(serverDesc, channelName, mediator, repositoryFactory) =>
      log.debug(s"Initializing channel message collector ($serverDesc channel $channelName)...")
      val collector = context.actorOf(Props(new ChannelMessageCollector(serverDesc, channelName, mediator, repositoryFactory)))
      context watch collector
      context become supervising(collector)
  }

  def supervising(collector: ActorRef): Receive = {
    case Terminated(_) =>
      log.debug("Channel message collector terminated, stopping supervisor...")
      context.stop(self)
    case any => collector ! any
  }

}

object ChannelMessageCollectorSupervisor {

  case class Initialize(serverDesc: ServerDesc, channelName: String, pubSubMediator: ActorRef, repositoryFactory: MessageRepositoryFactory)

  object Start

}
