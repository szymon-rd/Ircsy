package pl.jaca.ircsy.util.function

import scala.language.implicitConversions

/**
  * @author Jaca777
  *         Created 2016-10-23 at 12
  */
object FunctionConversions {
  def toJavaFunction[U, V](f: U => V): java.util.function.Function[_ >: U, _ <: V] = new java.util.function.Function[U, V] {
    override def apply(t: U): V = f(t)
  }
}
