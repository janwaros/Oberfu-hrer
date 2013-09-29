import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.SkypeException;
import org.picocontainer.Startable;
import bot.framework.components.skype.Skype;
import bot.framework.components.skype.StandardChat;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 29.08.13
 * Time: 10:48
 */
public class MeetingInformer implements ChatMessageListener, Startable {

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture task;
    private final static String UPDATE_COMMAND = "!meeting";
    private long lastCommand = System.currentTimeMillis();
    private long timeout = 5000; //5s
    private int lastDaySent = -1;

    public void start() throws SkypeException {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek==4) {
            Skype.getInstance().sendToStandardChat(StandardChat.WORK_CHAT, prepareMessage());
            lastDaySent = c.get(Calendar.DAY_OF_MONTH);
        }
        rescheduleTask();
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rescheduleTask() {
        if(task!=null) {
            task.cancel(false);
        }
        task = executor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                try {
                    Skype.getInstance().sendToStandardChat(StandardChat.WORK_CHAT, prepareMessage());
                    System.out.println("Send MeetingInformer fired");
                } catch (SkypeException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        },3,3, TimeUnit.HOURS);
    }

    private String prepareMessage() {
        return "(Robot) text""
    }


    @Override
    public void chatMessageReceived(ChatMessage chatMessage) {
        processChatMessage(chatMessage);
    }

    @Override
    public void chatMessageSent(ChatMessage chatMessage) {
        try {
            processChatMessage(chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void processChatMessage(ChatMessage chatMessage) {
        try {
            if(chatMessage.getContent().equals(UPDATE_COMMAND) && (lastCommand+timeout)<System.currentTimeMillis()) {
                chatMessage.getChat().send(prepareMessage());
                rescheduleTask();
                lastCommand=System.currentTimeMillis();
            }
        } catch (SkypeException e) {
            e.printStackTrace();
        }
    }
}
