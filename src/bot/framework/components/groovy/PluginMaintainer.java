package bot.framework.components.groovy;

import bot.framework.components.container.StartupListener;
import bot.framework.components.skype.Skype;
import bot.framework.plugin.BotPlugin;
import groovy.lang.GroovyClassLoader;
import org.apache.log4j.Logger;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.annotations.Inject;
import org.quartz.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 26.09.2013
 * Time: 23:22
 */
public class PluginMaintainer implements StartupListener, Job {

    Logger logger = Logger.getLogger(PluginMaintainer.class);

    @Inject Scheduler scheduler;
    @Inject PluginScheduler pluginScheduler;
    @Inject MutablePicoContainer container;

    File pluginsDirectory = new File("plugins/");
    JobKey thisJob = JobKey.jobKey(getClass().getName());


    private Map<String, PluginContainer> loadedPlugins = new HashMap<String, PluginContainer>();


    @Override
    public void start() {
        JobDetail check = JobBuilder.newJob(PluginMaintainer.class).withIdentity(thisJob).build();
        Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(30)).build();
        try {
            scheduler.scheduleJob(check,trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("PluginMaintainer failed to start,", e);
        }
    }

    @Override
    public void stop() {
        try {
            scheduler.deleteJob(thisJob);
        } catch (SchedulerException e) {
            // nothing terrible should happen
        }
    }

    private void unregisterPlugin(String pluginName) {
        PluginContainer plugin = loadedPlugins.get(pluginName);
        pluginScheduler.uregisterJobs(plugin);
        loadedPlugins.remove(pluginName);
        logger.info("Plugin unregistered: [id:"+plugin.getPluginClass().getName()+"]");
    }

    private void registerPlugin(String pluginName, PluginContainer pluginContainer) throws InstantiationException, SchedulerException {
        pluginScheduler.registerJobs(pluginContainer);
        loadedPlugins.put(pluginName, pluginContainer);
        logger.info("New plugin loaded: [id:"+pluginContainer.getPluginClass().getName()+",author:"+pluginContainer.getMeta().author()+",version:"+pluginContainer.getMeta().version()+"]");
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

            List<File> fileList = Arrays.asList(pluginsDirectory.listFiles());

            for(String pluginName : loadedPlugins.keySet()) {
                if(!this.containsName(pluginName, fileList)) {
                    unregisterPlugin(pluginName);
                }
            }

            for (final File fileEntry : fileList) {
                if(fileEntry.getName().endsWith(".groovy")) {
                    PluginContainer plugin = loadedPlugins.get(fileEntry.getName());
                    if(plugin!=null) {
                        if(plugin.isModified(fileEntry)) {
                           unregisterPlugin(fileEntry.getName());
                        } else {
                            continue;
                        }
                    }

                    try {
                        plugin = PluginContainer.loadFromFile(fileEntry, container);
                        registerPlugin(fileEntry.getName(),plugin);
                    } catch (Exception e) {
                        logger.warn("Plugin failed to load: "+fileEntry.getName(),e);
                    }

                }
            }
    }

    private Boolean containsName(String name, List<File> list) {
        for (final File fileEntry : list) {
            if(fileEntry.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
