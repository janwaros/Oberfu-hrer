package bot.framework.components.container;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.quartz.JobDetail;
import org.picocontainer.PicoContainer;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 27.09.2013
 * Time: 01:55
 */

public class PicoContainerJobFactory implements JobFactory {

    PicoContainer container;

    public PicoContainerJobFactory(PicoContainer container) {
        this.container = container;
    }

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {

        final JobDetail jobDetail = triggerFiredBundle.getJobDetail();

        Job job = container.getComponent(jobDetail.getJobClass());
        if(job==null) {
            try {
                job = (Job)jobDetail.getJobClass().newInstance();
            } catch (Exception e) {
                throw new SchedulerException(e);
            }
        }

        return job;

    }

}
