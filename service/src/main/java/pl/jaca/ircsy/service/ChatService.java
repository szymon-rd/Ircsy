package pl.jaca.ircsy.service;

import pl.jaca.ircsy.chat.ServerDesc;

/**
 * @author Jaca777
 *         Created 2016-05-27 at 22
 */
public interface ChatService {
    IrcsyUser getUser(String name);
    IrcsyUser createUser(String name);
    UserMessageRepository getUserMessageRepository(String userName);
    ChannelMessageRepository getChannelMessageRepository(ServerDesc server, String channelName);
}
