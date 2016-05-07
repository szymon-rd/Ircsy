package pl.jaca.ircsy.clientnode.connection

/**
  * @author Jaca777
  *         Created 2016-05-05 at 13
  */
case class ServerDesc(host: String, port: Int) {
  override def toString = host + ":" + port
}
