package plugins

import bot.framework.components.jira.Jira
import bot.framework.components.skype.MessageReceivedListener
import bot.framework.components.skype.Skype;
import bot.framework.plugin.BotPlugin
import bot.framework.plugin.CronScheduled
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper
import com.skype.ChatMessage
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

/**
 * Created by IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 08.08.13
 * Time: 17:33
 */

@BotPlugin(author = "Jarosław Kościński", version = "0.5")
public class ReviewInformer implements MessageReceivedListener {

    Logger logger = Logger.getLogger(ReviewInformer.class)

    private final static String JSON_FEED_URL = "http://tapir/bot.framework.components.jira/rest/api/latest/search?jql=filter=%22Mediation%20-%20All%22%20AND%20status%20=%20%22Resolved%22%20and%20%28labels%20is%20empty%20or%20labels%20not%20in%20%28Waiting%29%29%20ORDER%20BY%20due%20ASC,%20priority%20DESC,%20fixVersion%20ASC";
    private final static String UPDATE_COMMAND = "!review";
    private final static String JIRA_ISSUE_PREFIX = "http://tapir/bot.framework.components.jira/browse/";
    private String jira_user;
    private String jira_pass;

    private Skype skype;

    public ReviewInformer(Jira jira, Skype skype) throws SkypeException {
        this.skype = skype;
    }

    @CronScheduled("* * */3 ? * * *")
    public void sendReviewInfo() {
        skype.sendToBookmarkedChat(StandardChat.WORK_CHAT, prepareRssMessage());
    }

    @Override
    void chatMessageReceived(ChatMessage chatMessage) {
        if(chatMessage.getContent().equals(UPDATE_COMMAND)) {
            chatMessage.getChat().send(prepareRssMessage());
        }
    }

    private String prepareRssMessage() {
        StringBuilder message = new StringBuilder("(Robot) Uwaga! Tasków do review: ");

        try {
            URL url = new URL (JSON_FEED_URL);
            String credentials = jira_user + ":" + jira_pass;
            String encoded = new BASE64Encoder().encode(credentials.getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoded);
            InputStream content = connection.getInputStream();


            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(content, JsonNode.class);
            JsonNode issuesList = rootNode.get("issues");

            if(issuesList.size()<2) return null;

            message.append(issuesList.size()).append("\n");

            for(JsonNode issue : issuesList) {
                JsonNode assigned = issue.get("fields").get("assignee");
                message.append(issue.get("fields").get("summary").asText() + ", przypisany: "+(assigned.isNull()?"NIKT":assigned.get("name").asText())+" ( " + JIRA_ISSUE_PREFIX + issue.get("key").asText() + " )\n");
            }

            content.close();
            return message.toString();

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }


}
