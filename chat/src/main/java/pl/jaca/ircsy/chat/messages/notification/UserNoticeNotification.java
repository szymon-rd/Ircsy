package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChatUser;
import pl.jaca.ircsy.chat.messages.Notification;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserNoticeNotification implements Notification {
    private ChatUser user;
    private String text;

    public UserNoticeNotification(ChatUser user, String text) {
        this.user = user;
        this.text = text;
    }

    public ChatUser getUser() {
        return user;
    }

    public String getText() {
        return text;
    }
}
