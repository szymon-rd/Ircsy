package pl.jaca.ircsy.util.future

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Jaca777
 *         Created 2015-12-23 at 18
 */
object FutureConversions {

  /**
   * Converts list of futures to future of lists.
    * Returned future completes when all futures in given list are completed.
   */
  def all[T](fs: TraversableOnce[Future[T]])(implicit executor: ExecutionContext): Future[List[T]] =
    fs.foldRight(Future(Nil:List[T]))((future, fs2) =>
      for {
        x <- future
        xs <- fs2
      } yield x :: xs)

  /**
    * Converts map with values of type future to future of map.
    * Returned future completes when all futures in given map are completed.
    */
  def allValues[K, T](fs: Map[K, Future[T]])(implicit executor: ExecutionContext): Future[Map[K, T]] =
    fs.foldRight(Future(Map.empty[K, T]))((entry, fs2) =>
      for {
        x <- entry._2
        xs <- fs2
      } yield xs + (entry._1 -> x))
}
