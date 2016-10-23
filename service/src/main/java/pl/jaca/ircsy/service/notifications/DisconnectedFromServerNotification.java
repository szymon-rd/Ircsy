package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-10-22 at 13
 */
public class DisconnectedFromServerNotification implements ApplicationNotification {

    private ServerDesc serverDesc;
    private String username;
    private LocalDateTime time;

    public DisconnectedFromServerNotification(ServerDesc serverDesc, String username, LocalDateTime time) {
        this.serverDesc = serverDesc;
        this.username = username;
        this.time = time;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }
}
