package pl.jaca.ircsy.service.distributed

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef}
import akka.cluster.{Cluster, ClusterEvent, Member}
import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp}
import pl.jaca.ircsy.service.distributed.ClientNodeProxy.ForwardToNode

import scala.collection.immutable.Queue
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-28 at 18
  */
class ClientNodeProxy extends Actor {

  val cluster = Cluster(context.system)
  cluster.subscribe(self, ClusterEvent.initialStateAsEvents, classOf[ClusterEvent.MemberRemoved], classOf[ClusterEvent.MemberUp])

  var clientNodes = Set[Member]()

  override def receive: Receive = awaitingProxy(Queue.empty)

  def awaitingProxy(messagesQueue: Queue[Any]): Receive = {
    if (clientNodes.isEmpty) {
      case MemberUp(member) =>
        if (member.hasRole("client-node")) {
          clientNodes += member
          context become memberProxy(member, messagesQueue)
        }
      case ForwardToNode(msg) => context become awaitingProxy(messagesQueue.enqueue(msg))
    } else memberProxy(clientNodes.head, messagesQueue)
  }

  def memberProxy(member: Member, messages: Queue[Any]): Receive = {
    val receptionist = getReceptionist(member)
    messages.foreach(self ! _)
    proxy(member, receptionist)
  }

  def getReceptionist(member: Member): ActorRef = {
    val future = context.actorSelection(member.address + "/user/frontend").resolveOne(5 seconds)
    Await.result(future, 5 seconds)
  }

  def proxy(member: Member, receptionist: ActorRef): Receive = {
    case MemberRemoved(removedMember, _) if removedMember.hasRole("client-node") =>
      clientNodes -= removedMember
      if (removedMember == member)
        context become awaitingProxy(Queue.empty)
    case MemberRemoved(_, _) => //ignore
    case MemberUp(addedMember) =>
      if (addedMember.hasRole("client-node"))
        clientNodes += member

    case ForwardToNode(msg) =>
      receptionist ! msg
  }
}

object ClientNodeProxy {
  case class ForwardToNode(msg: Any)

}