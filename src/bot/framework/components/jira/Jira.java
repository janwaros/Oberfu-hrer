package bot.framework.components.jira;

import bot.framework.components.container.StartupListener;

import java.io.Console;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 26.09.2013
 * Time: 21:06
 */
public class Jira implements StartupListener {

    private String username;
    private String password;

    private static URI jiraServerUri = URI.create("http://localhost:2990/jira");


    @Override
    public void start() {
        Console console = System.console();
//        username = console.readLine("Enter JIRA username: ");
  //      password = new String(console.readPassword("Enter JIRA password: "));
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
