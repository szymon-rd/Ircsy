package pl.jaca.ircsy.clientnode.connection.irc

import com.ircclouds.irc.api

import scala.concurrent.Promise
import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-13 at 18
  */
class IrcCallback[T] extends Promise[T] with api.Callback[T] {
  val promise = Promise[T]()

  override def onFailure(exception: Exception) = failure(exception)

  override def onSuccess(result: T) = success(result)

  override def future = promise.future

  override def tryComplete(result: Try[T]): Boolean = promise.tryComplete(result)

  override def isCompleted = promise.isCompleted
}
