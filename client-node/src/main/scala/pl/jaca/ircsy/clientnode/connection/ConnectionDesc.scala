package pl.jaca.ircsy.clientnode.connection

/**
  * @author Jaca777
  *         Created 2016-05-02 at 16
  */
case class ConnectionDesc(serverDesc: ServerDesc, username: String) extends Serializable{
  override def toString =  serverDesc + "@" + username
}
