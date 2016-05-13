package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChatUser;
import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserQuitNotification implements Notification {

    private LocalDate time;
    private ChatUser user;
    private String quitMsg;

    public UserQuitNotification(LocalDate time, ChatUser user, String quitMsg) {
        this.time = time;
        this.user = user;
        this.quitMsg = quitMsg;
    }

    @Override
    public LocalDate getTime() {
        return time;
    }

    public ChatUser getUser() {
        return user;
    }

    public String getQuitMsg() {
        return quitMsg;
    }
}
