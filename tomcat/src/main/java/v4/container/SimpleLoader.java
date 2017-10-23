package v4.container;

import org.apache.catalina.Container;
import org.apache.catalina.Loader;
import util.FileUtil;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by wangqi on 2017/10/18 下午8:39.
 */
public class SimpleLoader implements Loader {
    public static final String SERVLET_BASE = "demo.servlets.";

    URLClassLoader classLoader;
    Container container;
    boolean delegate;
    boolean reloadable;

    public SimpleLoader(){
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL repository = classLoader.getResource(""); // 当前Classloader的根目录
        classLoader = new URLClassLoader( new URL[] { repository } );
    }

    @Override
    public void backgroundProcess() {

    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public boolean getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getInfo() {
        return "SimpleLoader";
    }

    @Override
    public boolean getReloadable() {
        return this.reloadable;
    }

    @Override
    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void addRepository(String repository) {

    }

    @Override
    public String[] findRepositories() {
        return new String[0];
    }

    @Override
    public boolean modified() {
        return false;
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }
}
