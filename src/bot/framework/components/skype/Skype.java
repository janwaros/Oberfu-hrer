package bot.framework.components.skype;

import bot.framework.components.container.StartupListener;
import com.skype.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 08.08.13
 * Time: 17:34
 */
public class Skype implements StartupListener {

    Logger logger = Logger.getLogger(Skype.class);

    Chat workChatHandle = null;
    Chat spamChatHandle = null;

    public Skype(PropertiesConfiguration configuration) throws SkypeException {
        StandardChat.loadFromConfiguration(configuration);
        if(!com.skype.Skype.isRunning()) {
            throw new RuntimeException("Skype is not running");
        }
    }

    public void sendToBookmarkedChat(StandardChat chat, String message) throws SkypeException {
        Chat destination = workChatHandle;
        if(chat==StandardChat.SPAM_CHAT) destination = spamChatHandle;

        destination.send(message);
    }

    public void sendToBookmarkedChat(String chatName, String message) {
        try {
            for(Chat chat : com.skype.Skype.getAllBookmarkedChats()) {
                if(chatName.equals(chat.getWindowTitle())) {
                    chat.send(message);
                }
            }
        } catch (SkypeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try {
            for(Chat chat : com.skype.Skype.getAllBookmarkedChats()) {
                logger.info("Bookmarked chat: "+chat.getWindowTitle());
                if(StandardChat.SPAM_CHAT.is(chat)) {
                    logger.info("Bookmarked chat: "+chat.getWindowTitle()+" is now a SPAM chat");
                    spamChatHandle = chat;
                }
                if(StandardChat.WORK_CHAT.is(chat)) {
                    logger.info("Bookmarked chat: "+chat.getWindowTitle()+" is now a WORK chat");
                    workChatHandle = chat;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {

    }
}
