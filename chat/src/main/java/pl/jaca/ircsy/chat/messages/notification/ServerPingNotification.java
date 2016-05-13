package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.Notification;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ServerPingNotification implements Notification {
    private String text;

    public ServerPingNotification(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
