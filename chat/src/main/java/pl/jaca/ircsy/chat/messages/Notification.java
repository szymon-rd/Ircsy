package pl.jaca.ircsy.chat.messages;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public interface Notification extends Serializable {
    LocalDate getTime();
}
