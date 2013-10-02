package bot.framework.components.skype;

import com.skype.Chat;
import com.skype.SkypeException;
import org.apache.commons.configuration.PropertiesConfiguration;
import sun.plugin.dom.exception.InvalidStateException;

/**
 * Created by IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 25.09.13
 * Time: 14:43
 */
public enum StandardChat {

    WORK_CHAT("skype.workChat"),
    SPAM_CHAT("skype.spamChat");

    private String propertyKey;
    private String value = null;

    private StandardChat(String propertyKey ) {
        this.propertyKey = propertyKey;
    }

    public static void loadFromConfiguration(PropertiesConfiguration configuration) {
        WORK_CHAT.value = configuration.getString(WORK_CHAT.propertyKey);
        SPAM_CHAT.value = configuration.getString(SPAM_CHAT.propertyKey);
    }

    public Boolean is(Chat chat) throws SkypeException {
        if(value==null) {
            throw new InvalidStateException("Values not loaded!");
        }
        return value.equals(chat.getWindowTitle());
    }
}
