package chat;

import rx.Observable;

/**
 * @author Jaca777
 *         Created 2016-04-30 at 11
 */
public interface Conversation {
    /**
     * @return Name of the participant User was talking to.
     */
    String getParticipant();

    /**
     * @return Current observable messages.
     */
    Observable<Message> getMessages();

    /**
     * @return History of this conversation.
     */
    MessageRepository getMessageRepository();
}
