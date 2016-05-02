package pl.jaca.ircsy.clientnode.listening

/**
  * @author Jaca777
  *         Created 2016-05-02 at 16
  */
case class ChatConnectionDesc(host: String, port: String, username: String) {
  override def toString = host + ":" + port + "@" + username
}
