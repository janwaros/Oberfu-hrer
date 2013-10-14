package bot.framework.components.jira;

import bot.framework.components.container.StartupListener;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.Console;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 26.09.2013
 * Time: 21:06
 */
public class Jira implements StartupListener {

    private String username = null;
    private String password = null;

    private JiraRestClient jiraRestClient;

    private URI jiraServerUri;

    public Jira(PropertiesConfiguration configuration) {
        jiraServerUri = URI.create(configuration.getString("jira.url"));
    }


    @Override
    public void start() {
        username = readLine("Enter JIRA username: ",false);
        password = readLine("Enter JIRA password: ", true);

        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        jiraRestClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
    }

    @Override
    public void stop() {

    }

    private String readLine(String prompt, Boolean hidden) {
        String line = null;

        Console console = System.console();
        do {
            if(hidden) {
                line = new String(console.readPassword(prompt));
            } else {
                line = console.readLine(prompt);
            }
        } while(line==null || line.trim().equals(""));
        return line;
    }

    public JiraRestClient getJiraRestClient() {
        return jiraRestClient;
    }
}
