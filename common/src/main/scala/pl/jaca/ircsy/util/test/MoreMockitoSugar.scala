package pl.jaca.ircsy.util.test

import org.mockito.Matchers

import scala.reflect.ClassTag

/**
  * @author Jaca777
  *         Created 2016-05-02 at 01
  */
trait MoreMockitoSugar {
  def any[T](implicit typeTag: ClassTag[T]): T =
    Matchers.any[T](typeTag.runtimeClass.asInstanceOf[Class[T]])
  def equal[T](t: T) = Matchers.eq(t)
}
