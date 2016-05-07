package pl.jaca.ircsy.clientnode.messagescollection

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import org.scalatest.path

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class MessageCollector(observableConnectionSupervisor: ActorRef) extends PersistentActor {

  override def persistenceId: String = "Collector-" + context.self.path.name

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case _ =>
  }
}
