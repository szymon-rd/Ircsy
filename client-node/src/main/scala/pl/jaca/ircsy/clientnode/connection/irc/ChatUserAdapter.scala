package pl.jaca.ircsy.clientnode.connection.irc

import com.ircclouds.irc.api.domain.IRCUser
import pl.jaca.ircsy.chat.messages.ChatUser

/**
  * @author Jaca777
  *         Created 2016-05-13 at 23
  */
class ChatUserAdapter(user: IRCUser) extends ChatUser(user.getNick, user.getHostname, user.getIdent)
