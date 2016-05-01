package chat;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-04-30 at 11
 */
public interface Message {
    /**
     * @return Time the message was sent.
     */
    LocalDate getTime();

    /**
     * @return Name of the author of the message.
     */
    String getAuthor();

    /**
     * @return Content of the message.
     */
    String getText();
}
