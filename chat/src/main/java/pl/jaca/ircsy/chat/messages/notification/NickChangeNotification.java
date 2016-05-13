package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChatUser;
import pl.jaca.ircsy.chat.messages.Notification;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class NickChangeNotification implements Notification {
    private ChatUser user;
    private String oldNick;
    private String newNick;

    public NickChangeNotification(ChatUser user, String oldNick, String newNick) {
        this.user = user;
        this.oldNick = oldNick;
        this.newNick = newNick;
    }

    public ChatUser getUser() {
        return user;
    }

    public String getOldNick() {
        return oldNick;
    }

    public String getNewNick() {
        return newNick;
    }
}
