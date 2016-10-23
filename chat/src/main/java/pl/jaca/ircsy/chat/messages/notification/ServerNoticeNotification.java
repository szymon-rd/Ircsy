package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ServerNoticeNotification implements Notification {

    private ServerDesc serverDesc;
    private LocalDateTime time;
    private String text;

    public ServerNoticeNotification(ServerDesc serverDesc, LocalDateTime time, String text) {
        this.serverDesc = serverDesc;
        this.time = time;
        this.text = text;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}

