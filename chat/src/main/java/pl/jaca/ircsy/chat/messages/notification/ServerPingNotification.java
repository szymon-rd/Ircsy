package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ServerPingNotification implements Notification {

    private LocalDate time;
    private String text;

    public ServerPingNotification(LocalDate time, String text) {
        this.time = time;
        this.text = text;
    }

    @Override
    public LocalDate getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
