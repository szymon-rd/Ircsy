package pl.jaca.ircsy.util.config

import com.typesafe.config.{Config, ConfigObject}

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration

/**
  * @author Jaca777
  *         Created 2015-10-02 at 23
  */
object ConfigUtil {
  implicit class Configuration(config: Config) {
    val stringAt: String => Option[String] =
      mapUnitPath(config.getString)

    val stringsAt: String => Option[Seq[String]] =
      mapUnitPath(config.getStringList _ andThen (_.asScala))

    val intAt: String => Option[Int] =
      mapUnitPath(config.getInt)

    val boolAt: String => Option[Boolean] =
      mapUnitPath(config.getBoolean)

    val objectAt: String => Option[ConfigObject] =
      mapUnitPath(config.getObject)

    def unitPathAt(path: String): Option[String] =
      if (config.hasPath(path)) Some(path) else None

    def mapUnitPath[T](f: String => T)(path: String) = unitPathAt(path) map f

    private implicit def java2scalaDuration(duration: java.time.Duration): Duration =
      Duration.fromNanos(duration.toNanos)
  }

}
