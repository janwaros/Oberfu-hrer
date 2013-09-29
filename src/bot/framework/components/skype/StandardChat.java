package bot.framework.components.skype;

/**
 * Created by IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 25.09.13
 * Time: 14:43
 */
public enum StandardChat {

    WORK_CHAT("Mediacja - Tutaj 90% powagi"),
    SPAM_CHAT("Tutaj wklejamy SPAM, sprosne linki, rasistowskie..");

    private String identifier;

    private StandardChat(String identifier ) {
        this.identifier = identifier;
    }

    public Boolean is(String chat) {
        return identifier.equals(chat);
    }
}
