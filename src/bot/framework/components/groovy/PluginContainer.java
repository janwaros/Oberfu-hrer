package bot.framework.components.groovy;

import bot.framework.plugin.BotPlugin;
import groovy.lang.GroovyClassLoader;
import org.picocontainer.PicoContainer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 29.09.2013
 * Time: 19:11
 */
public class PluginContainer {

    private BotPlugin meta = null;
    private Object instance = null;
    private long lastModified = 0;
    private PicoContainer container;

    private Class<?> clazz = null;

    public static PluginContainer loadFromFile(File file, PicoContainer container) throws IOException, IllegalArgumentException {
        return new PluginContainer(file, container);
    }

    private PluginContainer(File groovyFile, PicoContainer container) throws IOException, IllegalArgumentException {
        this.container = container;
        if(groovyFile.getName().endsWith(".groovy")) {
            this.lastModified = groovyFile.lastModified();
            GroovyClassLoader classLoader = new GroovyClassLoader();
            clazz = classLoader.parseClass(groovyFile);
            meta = clazz.getAnnotation(BotPlugin.class);
            if(meta==null) {
                throw new IllegalArgumentException("This is not a Bot plugin: "+groovyFile);
            }

        } else {
            throw new IllegalArgumentException("This is not a groovy file: "+groovyFile);
        }
    }

    public Object getInstance() throws InstantiationException {
        if(instance==null) {
            instance = this.createInstance();
        }
        return instance;
    }

    private Object createInstance() throws InstantiationException {
        for(Constructor<?> constructor : clazz.getConstructors()) {
            List parameters = new LinkedList<Object>();
            for(Class<?> parameter : constructor.getParameterTypes()) {
                Object obj = container.getComponent(parameter);
                if(obj==null) {
                    break;
                }
                parameters.add(obj);
            }
            if(parameters.size()==constructor.getParameterTypes().length) {
                try {
                    return constructor.newInstance(parameters.toArray());
                } catch (Exception e) {
                    continue;
                }
            }
        }
        throw new InstantiationException("Could not instantiate plugin with any of declared constructors");
    }

    public Boolean isModified(File file) {
        return file.lastModified()!=lastModified;
    }

    public BotPlugin getMeta() {
        return meta;
    }

    public Class<?> getPluginClass() {
        return clazz;
    }


}
