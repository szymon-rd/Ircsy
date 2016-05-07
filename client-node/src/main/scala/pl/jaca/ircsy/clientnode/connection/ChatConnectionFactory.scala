package pl.jaca.ircsy.clientnode.connection

/**
  * @author Jaca777
  *         Created 2016-05-02 at 13
  */
trait ChatConnectionFactory extends Serializable {
  def newConnection(): ChatConnection
}
