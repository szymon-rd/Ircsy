package chat;

import java.util.Optional;

/**
 * @author Jaca777
 *         Created 2016-04-30 at 10
 */
public interface Server {
    /**
     * Creates new user and joins the server.
     * @param userName
     * @return Created user.
     */
    User join(String userName);

    /**
     * Tries to get controllable user.
     * @param userName
     * @return
     */
    Optional<User> getUser(String userName);

    /**
     * If the channel exists and is observed, the channel; otherwise,
     * creates the channel and starts the observation.
     * @param channelName
     * @return Created or existing channel.
     */
    Channel getChannel(String channelName);
}
