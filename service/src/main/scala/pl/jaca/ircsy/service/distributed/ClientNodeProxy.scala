package pl.jaca.ircsy.service.distributed

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef}
import akka.cluster.{Cluster, ClusterEvent, Member}
import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp}

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

  override def receive: Receive = awaitingProxy

  def awaitingProxy: Receive = {
    if (clientNodes.isEmpty) {
      case MemberUp(member) =>
        if (member.hasRole("client-node")) {
          clientNodes += member
          context become memberProxy(member)
        }
    } else memberProxy(clientNodes.head)
  }

  def memberProxy(member: Member): Receive = {
    val receptionist = getReceptionist(member)
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
        context become awaitingProxy
    case MemberRemoved(_, _) => //ignore
  }
}
