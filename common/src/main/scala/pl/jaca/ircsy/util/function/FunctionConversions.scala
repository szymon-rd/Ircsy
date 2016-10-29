package pl.jaca.ircsy.util.function

import scala.language.implicitConversions
import java.util.function.{Function => JavaFunction}
/**
  * @author Jaca777
  *         Created 2016-10-23 at 12
  */
object FunctionConversions {
  implicit def toJavaFunction[U, V](f:Function1[U,V]): JavaFunction[U, V] = new JavaFunction[U, V] {
    override def apply(t: U): V = f(t)

    override def compose[T](before:JavaFunction[_ >: T, _ <: U]): JavaFunction[T, V] =
      toJavaFunction(f.compose(x => before.apply(x)))

    override def andThen[W](after:JavaFunction[_ >: V, _ <: W]): JavaFunction[U, W] =
      toJavaFunction(f.andThen(x => after.apply(x)))
  }

  implicit def fromJavaFunction[U, V](f:JavaFunction[U,V]): Function1[U, V] = f.apply
}
