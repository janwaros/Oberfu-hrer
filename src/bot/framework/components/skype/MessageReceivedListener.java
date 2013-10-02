package bot.framework.components.skype;

import com.skype.ChatMessage;
import com.skype.SkypeException;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 26.09.2013
 * Time: 21:36
 */
public interface MessageReceivedListener  {

    /**
     * This method is called when a ChatMessage is received.
     * @param receivedChatMessage the received message.
     * @throws com.skype.SkypeException when a connection has gone bad.
     */
    void chatMessageReceived(ChatMessage receivedChatMessage) throws SkypeException;
}
