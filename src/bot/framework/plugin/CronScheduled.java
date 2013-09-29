package bot.framework.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 26.09.2013
 * Time: 22:00
 */

/**
 * Used to schedule execution of given method with cron compatible schema
 * For more info on schema please refer to
 * http://quartz-scheduler.org/documentation/quartz-2.2.x/tutorials/crontrigger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CronScheduled {
    String value();
}
