package pl.jaca.ircsy.chat;

import rx.Observable;

import java.util.List;

/**
 * @author Jaca777
 *         Created 2016-04-30 at 11
 */
public interface User {

    Channel joinChannel(String channelName);

    /**
     * @return Channels the user is on.
     */
    List<Channel> getActiveChannels();

    Observable<String> getCommandReplies();

    Observable<Conversation> getConversations();

    /**
     * @param userName
     * @return Starts a conversation with User
     */
    Conversation startConversation(String userName);
}
