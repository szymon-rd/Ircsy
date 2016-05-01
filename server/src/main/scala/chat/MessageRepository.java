package chat;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Jaca777
 *         Created 2016-04-30 at 12
 */
public interface MessageRepository {

    /**
     * @param from
     * @param until
     * @return Messages in the repository sent between @from and @until dates.
     */
    List<Message> getMessages(LocalDate from, LocalDate until);

    /**
     * @param maxCount Maximal count of messages.
     * @return The most recent messages in the repository.
     */
    List<Message> getLastMessages(int maxCount);
}
