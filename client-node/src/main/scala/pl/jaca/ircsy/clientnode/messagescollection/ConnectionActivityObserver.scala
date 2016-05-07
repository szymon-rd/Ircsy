package pl.jaca.ircsy.clientnode.messagescollection

import akka.actor.{ActorRef, Actor}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import pl.jaca.ircsy.clientnode.connection.{ConnectionObservableProxy, ServerDesc, ConnectionDesc}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{ProxyState, OnRegisterStateSubject, LeftChannel, JoinedChannel}
import pl.jaca.ircsy.clientnode.messagescollection.ConnectionActivityObserver.{ChannelConnectionFound, FindChannelConnection}
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, Observer, RegisterObserver}


/**
  * @author Jaca777
  *         Created 2016-05-05 at 12
  */
class ConnectionActivityObserver(proxy: ActorRef, pubSubMediator: ActorRef) extends Actor {

  val observer = Observer(self, ClassFilterSubject(classOf[JoinedChannel], classOf[LeftChannel]), OnRegisterStateSubject)
  proxy ! RegisterObserver(observer)

  override def receive: Receive = {
    case ProxyState(_, connectionDesc, _, channels, _) =>
      context become observing(connectionDesc.serverDesc, channels)
      pubSubMediator ! Subscribe("channels-" + connectionDesc.serverDesc, self)
  }

  def observing(server: ServerDesc, channels: Set[String]): Receive = {
    case JoinedChannel(channel) => context become observing(server, channels + channel)
    case LeftChannel(channel) => context become observing(server, channels - channel)

    case FindChannelConnection(desc, channel) =>
      if (desc == server && (channels contains channel))
        pubSubMediator ! Publish("channels-" + desc, ChannelConnectionFound(desc, channel, proxy))
  }
}

object ConnectionActivityObserver {

  case class FindChannelConnection(serverDesc: ServerDesc, channelName: String)

  case class ChannelConnectionFound(serverDesc: ServerDesc, channelName: String, proxy: ActorRef)

}
