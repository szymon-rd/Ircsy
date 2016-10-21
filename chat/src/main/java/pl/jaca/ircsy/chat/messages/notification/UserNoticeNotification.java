package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.ChatUser;
import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserNoticeNotification implements Notification {

    private ServerDesc serverDesc;
    private LocalDate time;
    private ChatUser user;
    private String text;

    public UserNoticeNotification(ServerDesc serverDesc, LocalDate time, ChatUser user, String text) {
        this.serverDesc = serverDesc;
        this.time = time;
        this.user = user;
        this.text = text;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
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
