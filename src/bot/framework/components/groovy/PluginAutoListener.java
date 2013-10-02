package bot.framework.components.groovy;

import bot.framework.components.container.StartupListener;
import bot.framework.components.skype.MessageReceivedListener;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 02.10.2013
 * Time: 01:42
 */
public class PluginAutoListener implements StartupListener, ChatMessageListener {

    Logger logger = Logger.getLogger(PluginAutoListener.class);

    List<PluginContainer> skypeListeners = new LinkedList<PluginContainer>();

    public void registerListener(PluginContainer plugin) {

        if(Arrays.asList(plugin.getPluginClass().getInterfaces()).contains(MessageReceivedListener.class)) {
            skypeListeners.add(plugin);
            logger.info("Registered skype listener for "+plugin.getPluginClass().getName());
        }

    }

    public void unregisterListener(PluginContainer plugin) {
        if(skypeListeners.contains(plugin)) {
            skypeListeners.remove(plugin);
            logger.info("Unregistered skype listener for "+plugin.getPluginClass().getName());
        }
    }

    @Override
    public void start() {
        try {
            com.skype.Skype.addChatMessageListener(this);
        } catch (SkypeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        for (PluginContainer plugin : skypeListeners) {
            com.skype.Skype.removeChatMessageListener(this);
        }
    }

    @Override
    public void chatMessageReceived(ChatMessage chatMessage) throws SkypeException {
        if(chatMessage.getStatus()== ChatMessage.Status.RECEIVED) {
            for(PluginContainer plugin : skypeListeners) {
                try {
                    ((MessageReceivedListener)plugin.getInstance()).chatMessageReceived(chatMessage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void chatMessageSent(ChatMessage chatMessage) throws SkypeException {

    }
}
