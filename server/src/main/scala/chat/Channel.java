package chat;

import rx.Observable;

import java.util.List;

/**
 * @author Jaca777
 *         Created 2016-04-30 at 11
 */
public interface Channel {

    Observable<Message> getMessages();

    /**
     * @return Repository of all recorded messages.
     */
    MessageRepository getMessageRepository();

    /**
     * @return Names of the users on the channel.
     */
    List<String> getUsers();
}
