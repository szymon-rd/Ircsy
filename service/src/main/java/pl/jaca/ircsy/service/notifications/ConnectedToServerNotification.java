package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-06-10 at 21
 */
public class ConnectedToServerNotification implements ApplicationNotification{

    private ServerDesc serverDesc;
    private String username;
    private LocalDate time;

    public ConnectedToServerNotification(ServerDesc serverDesc, String username, LocalDate time) {
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
    public LocalDate getTime() {
        return time;
    }
}
