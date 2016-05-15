package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, Cancellable, Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.persistence.PersistentActor
import org.scalatest.path
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{ChannelSubject, ChannelMessageReceived, LeftChannel}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.ForwardToProxy
import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollector.Stop
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionProxyPublisher.{ChannelConnectionFound, FindChannelConnection, FindUserConnection}
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{UnregisterObserver, Observer, RegisterObserver}
import pl.jaca.ircsy.util.config.ConfigUtil.Configuration

import scala.concurrent.duration.{FiniteDuration, Duration}

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class ChannelMessageCollector(serverDesc: ServerDesc, channelName: String, pubSubMediator: ActorRef, repositoryFactory: MessageRepositoryFactory) extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  val config = context.system.settings.config
  val broadcastInterval = Duration.fromNanos(config.getDuration("app.collector.broadcast-interval").toNanos)
  val repository = repositoryFactory.newRepository()
  val observer = Observer(self, Set(ChannelSubject(channelName)))

  log.debug(s"Starting channel message collector ($serverDesc channel $channelName), subscribing to channel topic...")
  pubSubMediator ! Subscribe(s"channels-$serverDesc", self)

  override def receive: Receive = lookingForProxy()

  def lookingForProxy(broadcasting: Cancellable): Receive = {
    case ChannelConnectionFound(`channelName`, connection, sharding) if connection.serverDesc == serverDesc =>
      log.debug(s"Channel connection found ($serverDesc channel $channelName), registering observer...")
      sharding ! ForwardToProxy(connection, RegisterObserver(observer))
      context become collecting(connection, sharding)
    case Stop =>
      log.debug(s"Stopping collector ($serverDesc channel $channelName) during looking for proxy...")
      stop()
  }

  def lookingForProxy(): Receive = {
    log.debug(s"Starting looking for proxy connected to ($serverDesc channel $channelName)...")
    val broadcastMessage = Publish(s"channels-$serverDesc", FindChannelConnection(serverDesc, channelName))
    val broadcasting = context.system.scheduler.schedule(Duration.Zero, broadcastInterval, pubSubMediator, broadcastMessage)
    lookingForProxy(broadcasting)
  }

  def collecting(connection: ConnectionDesc, sharding: ActorRef): Receive = {
    case ChannelMessageReceived(message) =>
      repository.addChannelMessage(serverDesc, message)
    case LeftChannel => context become lookingForProxy()
    case Stop =>
      log.debug(s"Stopping collector ($serverDesc channel $channelName) during collection...")
      sharding ! ForwardToProxy(connection, UnregisterObserver(observer))
      stop()
  }

  def stop() = context.stop(self)

}

object ChannelMessageCollector {

  object Stop

}

