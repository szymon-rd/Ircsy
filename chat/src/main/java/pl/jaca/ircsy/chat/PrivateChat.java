package pl.jaca.ircsy.chat;

/**
 * @author Jaca777
 *         Created 2016-05-14 at 11
 */
public class PrivateChat {
    private String mainParticipantName;
    private String secondParticipantName;

    public PrivateChat(String mainParticipantName, String secondParticipantName) {
        this.mainParticipantName = mainParticipantName;
        this.secondParticipantName = secondParticipantName;
    }

    public String getMainParticipantName() {
        return mainParticipantName;
    }

    public String getSecondParticipantName() {
        return secondParticipantName;
    }
}
