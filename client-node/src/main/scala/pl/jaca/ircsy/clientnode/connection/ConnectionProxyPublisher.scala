package pl.jaca.ircsy.clientnode.connection

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{JoinedChannel, LeftChannel, OnRegisterStateSubject, ProxyState}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyPublisher._
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.ForwardToProxy
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, Observer, RegisterObserver, UnregisterObserver}


/**
  * @author Jaca777
  *         Created 2016-05-05 at 12
  */
class ConnectionProxyPublisher(connection: ConnectionDesc, proxy: ActorRef, pubSubMediator: ActorRef) extends Actor with ActorLogging {

  log.debug(s"Starting connection proxy publisher ($connection), registering observer...")
  val observer = Observer(self, subjects = Set(ClassFilterSubject(classOf[JoinedChannel], classOf[LeftChannel]), OnRegisterStateSubject))
  proxy ! RegisterObserver(observer)


  override def receive: Receive = {
    case ProxyState(_, connectionDesc, _, channels, _) =>
      context become observing(connectionDesc, channels)
      pubSubMediator ! Subscribe(s"channels-${connectionDesc.getServer}", self)
      pubSubMediator ! Subscribe(s"users-${connectionDesc.getServer}", self)
  }

  def observing(connection: ConnectionDesc, channels: Set[String]): Receive = {
    case JoinedChannel(server, channel) => context become observing(connection, channels + channel)
    case LeftChannel(server, channel) => context become observing(connection, channels - channel)

    case FindChannelConnection(desc, channel) =>
      if (desc == connection.getServer && (channels contains channel))
        pubSubMediator ! Publish(s"channels-$desc", ChannelConnectionFound(channel, connection, proxy))

    case FindUserConnection(desc) =>
      if(desc == connection)
        pubSubMediator ! Publish(s"users-${desc.getServer}", UserConnectionFound(desc, proxy))

    case Stop =>
      log.debug(s"Stopping connection proxy publisher ($connection)...")
      proxy ! UnregisterObserver(observer)
  }
}

object ConnectionProxyPublisher {

  object Stop

  case class FindChannelConnection(serverDesc: ServerDesc, channelName: String)

  case class ChannelConnectionFound(channelName: String, connectionDesc: ConnectionDesc, proxy: ActorRef)

  case class FindUserConnection(connectionDesc: ConnectionDesc)

  case class UserConnectionFound(connectionDesc: ConnectionDesc, proxy: ActorRef)
}
