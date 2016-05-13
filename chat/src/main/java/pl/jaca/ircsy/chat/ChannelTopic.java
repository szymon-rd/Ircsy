package pl.jaca.ircsy.chat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 23
 */
public class ChannelTopic implements Serializable {
    private LocalDate time;
    private String setBy;
    private String text;

    public ChannelTopic(LocalDate time, String setBy, String text) {
        this.time = time;
        this.setBy = setBy;
        this.text = text;
    }

    public LocalDate getTime() {
        return time;
    }

    public String getSetBy() {
        return setBy;
    }

    public String getText() {
        return text;
    }
}
