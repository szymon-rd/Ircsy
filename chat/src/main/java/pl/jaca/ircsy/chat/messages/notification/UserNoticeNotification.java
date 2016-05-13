package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChatUser;
import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserNoticeNotification implements Notification {

    private LocalDate time;
    private ChatUser user;
    private String text;

    public UserNoticeNotification(LocalDate time, ChatUser user, String text) {
        this.time = time;
        this.user = user;
        this.text = text;
    }

    @Override
    public LocalDate getTime() {
        return time;
    }

    public ChatUser getUser() {
        return user;
    }

    public String getText() {
        return text;
    }
}
