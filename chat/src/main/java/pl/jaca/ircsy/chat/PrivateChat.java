package pl.jaca.ircsy.chat;

import pl.jaca.ircsy.chat.messages.ChatUser;

import java.io.Serializable;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class PrivateChat implements Serializable {

    private ChatUser mainParticipant;
    private ChatUser secondParticipant;

    public PrivateChat(ChatUser mainParticipant, ChatUser secondParticipant) {
        this.mainParticipant = mainParticipant;
        this.secondParticipant = secondParticipant;
    }

    public ChatUser getMainParticipant() {
        return mainParticipant;
    }

    public ChatUser getSecondParticipant() {
        return secondParticipant;
    }
}
