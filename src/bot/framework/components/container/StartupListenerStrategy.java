package bot.framework.components.container;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.LifecycleComponentMonitor;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 27.09.2013
 * Time: 01:29
 */
public class StartupListenerStrategy extends StartableLifecycleStrategy {
    public StartupListenerStrategy() {
        super(new LifecycleComponentMonitor());
    }

    protected java.lang.Class getStartableInterface() {
        return StartupListener.class;
    }

}
