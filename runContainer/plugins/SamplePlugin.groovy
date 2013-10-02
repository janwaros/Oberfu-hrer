import bot.framework.components.skype.MessageReceivedListener
import bot.framework.components.skype.Skype
import bot.framework.components.skype.StandardChat
import bot.framework.plugin.BotPlugin
import bot.framework.plugin.CronScheduled
import com.skype.ChatMessage
import com.skype.SkypeException

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 02.10.2013
 * Time: 02:04
 */

@BotPlugin(author = "Jarek", version = "1.0")
class SamplePlugin implements MessageReceivedListener {

    Skype skype;

    public SamplePlugin(Skype skype) {
        this.skype = skype;
    }

    void chatMessageReceived(ChatMessage receivedChatMessage) throws SkypeException {
        receivedChatMessage.getChat().send("Dzieki za wiadomosc");
    }

    @CronScheduled("0 * * ? * * *")
    void sendPing() {
        skype.sendToBookmarkedChat(StandardChat.SPAM_CHAT, "ping pong"); // wysylaj co minute
    }
}