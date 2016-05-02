package pl.jaca.ircsy.clientnode.listening

/**
  * @author Jaca777
  *         Created 2016-05-02 at 13
  */
abstract class ChatConnectionFactory extends Serializable {
  def getConnection(): ChatConnection
}
