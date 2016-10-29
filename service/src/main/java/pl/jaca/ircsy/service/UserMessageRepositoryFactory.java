package pl.jaca.ircsy.service;

import pl.jaca.ircsy.chat.ServerDesc;

/**
 * @author Jaca777
 *         Created 2016-06-05 at 13
 */
public interface UserMessageRepositoryFactory {
    UserMessageRepository newRepository(ServerDesc serverDesc, String username);
}
