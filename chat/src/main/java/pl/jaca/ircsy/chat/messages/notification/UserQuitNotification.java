package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChatUser;
import pl.jaca.ircsy.chat.messages.Notification;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserQuitNotification implements Notification {
    private ChatUser user;
    private String quitMsg;

    public UserQuitNotification(ChatUser user, String quitMsg) {
        this.user = user;
        this.quitMsg = quitMsg;
    }

    public ChatUser getUser() {
        return user;
    }

    public String getQuitMsg() {
        return quitMsg;
    }
}
