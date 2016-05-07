package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionActivityObserver.{ChannelConnectionFound, FindChannelConnection, FindUserConnection, UserConnectionFound}
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, Observer, RegisterObserver}


/**
  * @author Jaca777
  *         Created 2016-05-05 at 12
  */
class ConnectionActivityObserver(proxy: ActorRef, pubSubMediator: ActorRef) extends Actor {

  val observer = Observer(self, subjects = Set(ClassFilterSubject(classOf[JoinedChannel], classOf[LeftChannel]), OnRegisterStateSubject))
  proxy ! RegisterObserver(observer)

  override def receive: Receive = {
    case ProxyState(_, connectionDesc, _, channels, _) =>
      context become observing(connectionDesc, channels)
      pubSubMediator ! Subscribe("channels-" + connectionDesc.serverDesc, self)
  }

  def observing(connection: ConnectionDesc, channels: Set[String]): Receive = {
    case JoinedChannel(channel) => context become observing(connection, channels + channel)
    case LeftChannel(channel) => context become observing(connection, channels - channel)

    case FindChannelConnection(desc, channel) =>
      if (desc == connection.serverDesc && (channels contains channel))
        pubSubMediator ! Publish(s"channels-$desc", ChannelConnectionFound(desc, channel, proxy))

    case FindUserConnection(desc, name) =>
      if(desc == connection.serverDesc && name == connection.username)
        pubSubMediator ! Publish(s"users-$desc", UserConnectionFound(desc, name, proxy) )
  }
}

object ConnectionActivityObserver {

  case class FindChannelConnection(serverDesc: ServerDesc, channelName: String)

  case class ChannelConnectionFound(serverDesc: ServerDesc, channelName: String, proxy: ActorRef)

  case class FindUserConnection(serverDesc: ServerDesc, userName: String)

  case class UserConnectionFound(serverDesc: ServerDesc, userName: String, proxy: ActorRef)
}
