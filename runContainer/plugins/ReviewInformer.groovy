package plugins

import bot.framework.components.jira.Jira
import bot.framework.components.skype.MessageReceivedListener
import bot.framework.components.skype.Skype
import bot.framework.components.skype.StandardChat;
import bot.framework.plugin.BotPlugin
import bot.framework.plugin.CronScheduled
import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.BasicIssue
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.SearchResult
import com.atlassian.util.concurrent.Promise
import com.google.common.collect.Lists
import com.skype.ChatMessage
import com.skype.SkypeException;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 08.08.13
 * Time: 17:33
 */

@BotPlugin(author = "Jarosław Kościński", version = "0.5")
public class ReviewInformer implements MessageReceivedListener {

    Logger logger = Logger.getLogger(ReviewInformer.class)

    private final static String REVIEW_JIRA_MATCHER = "\"Mediation - All\" AND status = \"Resolved\" and (labels is empty or labels not in 'Waiting') ORDER BY due ASC, priority DESC, fixVersion ASC";
    private final static String UPDATE_COMMAND = "!review";
    private final static String JIRA_URL_ISSUE_PREFIX = "http://tapir/jira/browse/";
    private Skype skype;
    private JiraRestClient restClient;

    public ReviewInformer(Jira jira, Skype skype) throws SkypeException {
        this.skype = skype;
        this.restClient = jira.getJiraRestClient();
    }

    @CronScheduled("* * */3 ? * * *")
    public void sendReviewInfo() {
        skype.sendToBookmarkedChat(StandardChat.WORK_CHAT, prepareMessage());
    }

    @Override
    void chatMessageReceived(ChatMessage chatMessage) {
        if(chatMessage.getContent().contains(UPDATE_COMMAND)) {
            chatMessage.getChat().send(prepareMessage());
        }
    }

    private String prepareMessage() {
        StringBuilder message = new StringBuilder("(Robot) Uwaga! Tasków do review: ");

        try {
            final SearchResult searchResult = restClient.getSearchClient().searchJql(REVIEW_JIRA_MATCHER).claim();
            final IssueRestClient issueClient = restClient.getIssueClient();
            List<String> issues = new LinkedList<String>();

            for (BasicIssue basicIssue : searchResult.getIssues()) {
                StringBuilder issueText = new StringBuilder();
                Issue issue = issueClient.getIssue(basicIssue.getKey()).claim();
                issueText.append(issue.getSummary());
                issueText.append(", przypisany: "+(issue.getAssignee()==null?"NIKT":issue.getAssignee().toString()));
                issueText.append(" (" + JIRA_URL_ISSUE_PREFIX + issue.get("key").asText() + " )");
                issues.add(issueText.toString());
            }

            //if 2 or less, not a big deal
            if(issues.size()<=2) return "";

            message.append(issues.size()).append("\n");

            for(String issue : issues) {
                message.append(issue).append("\n");
            }

            return message.toString();

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }


}
