package pl.jaca.ircsy.chat;

/**
 * @author Jaca777
 *         Created 2016-05-28 at 16
 */
public class ConnectionDesc {
    private ServerDesc server;
    private String nickname;

    protected ConnectionDesc(){

    }

    public ConnectionDesc(ServerDesc server, String nickname) {
        this.server = server;
        this.nickname = nickname;
    }

    public ServerDesc getServer() {
        return server;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return server + "@" + nickname;
    }
}
