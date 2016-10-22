package pl.jaca.ircsy.clientnode.observableactor

import akka.actor.ActorRef

/**
  * @author Jaca777
  *         Created 2016-05-04 at 20
  */
object ObservableActorProtocol {
  trait ObserverCmd

  case class Observer(ref: ActorRef, subjects: Set[ObserverSubject]) {
    def isInterestedIn(msg: Any): Boolean =
      subjects.exists(_ isInterestedIn msg)
  }

  abstract class ObserverSubject {
    def isInterestedIn(msg: Any): Boolean
    def and(observerSubject: ObserverSubject) = new ObserverSubject {
      override def isInterestedIn(msg: Any): Boolean =
        this.isInterestedIn(msg) || observerSubject.isInterestedIn(msg)
    }
  }

  case class FilterSubject(filter: (Any => Boolean)) extends ObserverSubject {
    override def isInterestedIn(msg: Any): Boolean = filter(msg)
  }

  case class ClassFilterSubject(classes: Class[_]*) extends ObserverSubject{
    override def isInterestedIn(msg: Any): Boolean = classes contains msg.getClass
  }

  case class RegisterObserver(observer: Observer) extends ObserverCmd

  case class UnregisterObserver(observer: Observer) extends ObserverCmd

}
