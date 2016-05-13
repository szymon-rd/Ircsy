package pl.jaca.ircsy.chat.messages;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 23
 */
public class ChatUser {
    private String nick;
    private String hostname;
    private String ident;

    public ChatUser(String nick, String hostname, String ident) {
        this.nick = nick;
        this.hostname = hostname;
        this.ident = ident;
    }

    public String getNick() {
        return nick;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIdent() {
        return ident;
    }
}
