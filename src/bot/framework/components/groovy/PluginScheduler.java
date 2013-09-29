package bot.framework.components.groovy;

import bot.framework.plugin.CronScheduled;
import org.apache.log4j.Logger;
import org.picocontainer.annotations.Inject;
import org.quartz.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 29.09.2013
 * Time: 21:58
 */
public class PluginScheduler {

    Logger logger = Logger.getLogger(PluginScheduler.class);

    public static final String METHOD_KEY = "method";
    public static final String PLUGIN_KEY = "plugin";

    @Inject
    Scheduler scheduler;

    Map<PluginContainer, List<JobKey>> jobs = new HashMap<PluginContainer, List<JobKey>>();

    public void registerJobs(PluginContainer plugin) throws SchedulerException, InstantiationException {
        List<JobKey> newJobs = new LinkedList<JobKey>();
        for(Method method : plugin.getPluginClass().getMethods()) {

            CronScheduled annotation = method.getAnnotation(CronScheduled.class);
            if(annotation!=null && method.getParameterTypes().length==0) {
                JobKey thisJob = JobKey.jobKey(plugin.getPluginClass().getName()+"-"+method.getName());
                JobDetail methodExecution = JobBuilder.newJob(MethodExecutionJob.class).withIdentity(thisJob).build();
                methodExecution.getJobDataMap().put(METHOD_KEY,method);
                methodExecution.getJobDataMap().put(PLUGIN_KEY,plugin.getInstance());
                Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(annotation.value())).build();
                scheduler.scheduleJob(methodExecution,trigger);
                newJobs.add(thisJob);
            }
        }
        if(newJobs.size()>0) {
            jobs.put(plugin, newJobs);
        }
    }

    public void uregisterJobs(PluginContainer plugin) {
        List<JobKey> pluginJobs = jobs.get(plugin);

        try {
            if(pluginJobs!=null) {
                for(JobKey job : pluginJobs) {
                    scheduler.deleteJob(job);
                }
                jobs.remove(plugin);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MethodExecutionJob implements Job {

        Logger logger = Logger.getLogger(MethodExecutionJob.class);

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            try {
                Method method = (Method) jobExecutionContext.getJobDetail().getJobDataMap().get(METHOD_KEY);
                Object pluginInstance = jobExecutionContext.getJobDetail().getJobDataMap().get(PLUGIN_KEY);
                method.invoke(pluginInstance);
            } catch (Exception e) {
                throw new JobExecutionException(e);
            }
        }
    }

}
