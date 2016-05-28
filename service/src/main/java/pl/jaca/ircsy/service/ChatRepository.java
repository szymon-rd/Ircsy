package pl.jaca.ircsy.service;

import pl.jaca.ircsy.chat.ServerDesc;

/**
 * @author Jaca777
 *         Created 2016-05-28 at 16
 */
public interface ChatRepository {
    ChannelMessageRepository getChannelMessageRepositor(ServerDesc server, String channelName);
    UserMessageRepository getUserMessageRepository(String username);
}
