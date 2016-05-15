package pl.jaca.ircsy.chat;

/**
 * @author Jaca777
 *         Created 2016-05-14 at 11
 */
public class PrivateChat {
    private String mainParticiantName;
    private String secondParticipantName;

    public PrivateChat(String mainParticiantName, String secondParticipantName) {
        this.mainParticiantName = mainParticiantName;
        this.secondParticipantName = secondParticipantName;
    }

    public String getMainParticiantName() {
        return mainParticiantName;
    }

    public String getSecondParticipantName() {
        return secondParticipantName;
    }
}
