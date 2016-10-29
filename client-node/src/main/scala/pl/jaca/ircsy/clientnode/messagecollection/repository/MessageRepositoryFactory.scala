package pl.jaca.ircsy.clientnode.messagecollection.repository

/**
  * @author Jaca777
  *         Created 2016-05-07 at 21
  */

trait MessageRepositoryFactory {
  def newRepository(): MessageRepository
}
