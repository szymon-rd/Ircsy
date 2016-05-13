package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChatUser;
import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class NickChangeNotification implements Notification {

    private LocalDate time;
    private ChatUser user;
    private String oldNick;
    private String newNick;

    public NickChangeNotification(LocalDate time, ChatUser user, String oldNick, String newNick) {
        this.time = time;
        this.user = user;
        this.oldNick = oldNick;
        this.newNick = newNick;
    }

    @Override
    public LocalDate getTime() {
        return time;
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
