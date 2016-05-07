package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.{Actor, ActorRef}
import akka.persistence.PersistentActor
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory

/**
  * @author Jaca777
  *         Created 2016-05-07 at 21
  */
class PrivateMessageCollector(username: String, repositoryFactory: MessageRepositoryFactory, pubSubMediator: ActorRef) extends Actor{


  override def receive: Receive = {
    case _ =>
  }
}
