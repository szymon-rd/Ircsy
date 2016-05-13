package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.Notification;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ErrorNotification implements Notification {
    private String error;

    public ErrorNotification(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

