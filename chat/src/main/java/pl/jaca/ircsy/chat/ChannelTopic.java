package pl.jaca.ircsy.chat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 23
 */
public class ChannelTopic implements Serializable {
    private LocalDateTime time;
    private String setBy;
    private String text;

    public ChannelTopic(LocalDateTime time, String setBy, String text) {
        this.time = time;
        this.setBy = setBy;
        this.text = text;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getSetBy() {
        return setBy;
    }

    public String getText() {
        return text;
    }
}
