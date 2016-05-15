package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.{ActorLogging, Cancellable, Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.persistence.PersistentActor
import pl.jaca.ircsy.clientnode.connection.ConnectionDesc
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.ForwardToProxy
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionProxyPublisher.{FindUserConnection, UserConnectionFound, FindChannelConnection, ChannelConnectionFound}
import pl.jaca.ircsy.clientnode.messagecollection.PrivateMessageCollector.Stop
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, UnregisterObserver, RegisterObserver, Observer}

import scala.concurrent.duration.Duration

/**
  * @author Jaca777
  *         Created 2016-05-07 at 21
  */
class PrivateMessageCollector(connectionDesc: ConnectionDesc, pubSubMediator: ActorRef, repositoryFactory: MessageRepositoryFactory) extends Actor with ActorLogging{

  implicit val executionContext = context.dispatcher
  val config = context.system.settings.config
  val broadcastInterval = Duration.fromNanos(config.getDuration("app.collector.broadcast-interval").toNanos)
  val repository = repositoryFactory.newRepository()
  val observer = Observer(self, Set(ClassFilterSubject(classOf[PrivateMessageReceived], classOf[DisconnectedFromServer])))

  log.debug(s"Starting private message collector ($connectionDesc), subscribing to user topic ...")
  pubSubMediator ! Subscribe(s"users-${connectionDesc.serverDesc}", self)

  override def receive: Receive = lookingForProxy()

  def lookingForProxy(broadcasting: Cancellable): Receive = {
    case UserConnectionFound(`connectionDesc`, sharding) =>
      log.debug(s"Proxy ($connectionDesc) found, starting collection ...")
      sharding ! ForwardToProxy(connectionDesc, RegisterObserver(observer))
      context become collecting(sharding)
    case Stop =>
      log.debug(s"Stopping private messages collector ($connectionDesc) during looking for proxy...")
      stop()
  }

  def lookingForProxy(): Receive = {
    log.debug(s"Starting looking for proxy: ($connectionDesc)")
    val broadcastMessage = Publish(s"users-${connectionDesc.serverDesc}", FindUserConnection(connectionDesc))
    val broadcasting = context.system.scheduler.schedule(Duration.Zero, broadcastInterval, pubSubMediator, broadcastMessage)
    lookingForProxy(broadcasting)
  }

  def collecting(sharding: ActorRef): Receive = {
    case PrivateMessageReceived(message) =>
      repository.addPrivateMessage(connectionDesc.serverDesc, message)
    case DisconnectedFromServer(`connectionDesc`) =>
      context become lookingForProxy()
    case Stop =>
      log.debug(s"Stopping private messages collector ($connectionDesc) during collection...")
      sharding ! ForwardToProxy(connectionDesc, UnregisterObserver(observer))
      stop()
  }

  def stop() = context.stop(self)

}
object PrivateMessageCollector {
  object Stop
}
