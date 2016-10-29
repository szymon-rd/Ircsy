package pl.jaca.ircsy.chat;

/**
 * @author Jaca777
 *         Created 2016-05-14 at 11
 */
public class PrivateChat {
    private ServerDesc server;
    private String mainParticipantName;
    private String secondParticipantName;

    public PrivateChat(ServerDesc server, String mainParticipantName, String secondParticipantName) {
        this.server = server;
        this.mainParticipantName = mainParticipantName;
        this.secondParticipantName = secondParticipantName;
    }

    public ServerDesc getServer() {
        return server;
    }

    public String getMainParticipantName() {
        return mainParticipantName;
    }

    public String getSecondParticipantName() {
        return secondParticipantName;
    }
}
