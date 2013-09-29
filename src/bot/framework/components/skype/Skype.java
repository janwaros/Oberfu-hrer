package bot.framework.components.skype;

import bot.framework.components.container.StartupListener;
import com.skype.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 08.08.13
 * Time: 17:34
 */
public class Skype implements StartupListener {

    Chat workChatHandle = null;
    Chat spamChatHandle = null;

    public Skype() throws SkypeException {
        //if(!com.skype.Skype.isRunning()) {
       //     throw new RuntimeException("Skype is not runnig");
        //}
    }

    public void registerChatMessageListener(ChatMessageListener listener) throws SkypeException {
        com.skype.Skype.addChatMessageListener(listener);
    }

    public void sendToStandardChat(StandardChat chat, String message) throws SkypeException {
        Chat destination = workChatHandle;
        if(chat==StandardChat.SPAM_CHAT) destination = spamChatHandle;

        destination.send(message);
    }

    @Override
    public void start() {
//        try {
//            for(Chat chat : com.skype.Skype.getAllBookmarkedChats()) {
//                if(StandardChat.SPAM_CHAT.is(chat.getWindowTitle())) {
//                    spamChatHandle = chat;
//                }
//                if(StandardChat.WORK_CHAT.is(chat.getWindowTitle())) {
//                    workChatHandle = chat;
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public void stop() {

    }
}
