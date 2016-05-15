package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.{ActorLogging, Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.ForwardToProxy
import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionProxyPublisher.{ChannelConnectionFound, FindChannelConnection, FindUserConnection, UserConnectionFound}
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{UnregisterObserver, ClassFilterSubject, Observer, RegisterObserver}


/**
  * @author Jaca777
  *         Created 2016-05-05 at 12
  */
class ConnectionProxyPublisher(connection: ConnectionDesc, sharding: ActorRef, pubSubMediator: ActorRef) extends Actor with ActorLogging {

  log.debug(s"Starting connection proxy publisher ($connection), registering observer...")
  val observer = Observer(self, subjects = Set(ClassFilterSubject(classOf[JoinedChannel], classOf[LeftChannel]), OnRegisterStateSubject))
  sharding ! ForwardToProxy(connection, RegisterObserver(observer))


  override def receive: Receive = {
    case ProxyState(_, connectionDesc, _, channels, _) =>
      context become observing(connectionDesc, channels)
      pubSubMediator ! Subscribe(s"channels-${connectionDesc.serverDesc}", self)
      pubSubMediator ! Subscribe(s"users-${connectionDesc.serverDesc}", self)
  }

  def observing(connection: ConnectionDesc, channels: Set[String]): Receive = {
    case JoinedChannel(channel) => context become observing(connection, channels + channel)
    case LeftChannel(channel) => context become observing(connection, channels - channel)

    case FindChannelConnection(desc, channel) =>
      if (desc == connection.serverDesc && (channels contains channel))
        pubSubMediator ! Publish(s"channels-$desc", ChannelConnectionFound(channel, connection, sharding))

    case FindUserConnection(desc) =>
      if(desc == connection)
        pubSubMediator ! Publish(s"users-${desc.serverDesc}", UserConnectionFound(desc, sharding))

    case Stop =>
      log.debug(s"Stopping connection proxy publisher ($connection)...")
      sharding ! ForwardToProxy(connection, UnregisterObserver(observer))
  }
}

object ConnectionProxyPublisher {

  case class FindChannelConnection(serverDesc: ServerDesc, channelName: String)

  case class ChannelConnectionFound(channelName: String, connectionDesc: ConnectionDesc, sharding: ActorRef)

  case class FindUserConnection(connectionDesc: ConnectionDesc)

  case class UserConnectionFound(connectionDesc: ConnectionDesc, sharding: ActorRef)
}
