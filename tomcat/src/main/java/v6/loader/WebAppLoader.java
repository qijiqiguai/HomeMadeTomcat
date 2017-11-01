package v6.loader;

import org.apache.catalina.Container;
import org.apache.catalina.Loader;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wangqi
 * @date 2017/10/25 下午6:55
 *
 * Loader 的作用是提供 专用的ClassLoader, 用于加载 Servlet 和 Web应用 的应用类库，而不能加载其他的任何东西。
 * 这样可以提高安全性，Servlet中暴露的漏洞控制系统的 ClassLoader 进而对系统造成危害。
 *
 * 同时，开启后台线程定期刷新类文件，防止有更新发生。
 */
public class WebAppLoader implements Loader{
    ClassLoader classLoader;
    Container container;
    boolean delegate;
    boolean reloadable;
    List<String> repositories;

    /**
     * 有getClassLoader，却没有提供 setClassLoader方法，这表明ClassLoader是在内部实例化的，而不是从外界传入。
     */
    public WebAppLoader(){
        repositories = new ArrayList<>();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
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
        return this.delegate;
    }

    @Override
    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getInfo() {
        return "WebAppLoader";
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
    public void addRepository(String repository) {
        this.repositories.add(repository);
    }

    @Override
    public String[] findRepositories() {
        return repositories.toArray(new String[repositories.size()]);
    }

    @Override
    public boolean modified() {
        return false;
    }


    /**
     * 用于在后台线程中调用，主要用于Class修改的重新载入
     */
    @Override
    public void backgroundProcess() {

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

}
