package v5.lifecycle;

import org.apache.catalina.*;
import org.apache.catalina.util.LifecycleSupport;

/**
 * Created by wangqi on 2017/10/24 下午8:52.
 *
 *
 * Lifecycle 本质上是很简单的，实现了Lifecycle的接口可以有几个核心方法：start, stop, init, destroy，LifecycleListener相关的增删查。
 * 同时，提供了几个核心的事件定义：INIT，START，STOP，DESTORY，以及相关的 BEFORE & After。
 * 在继承的核心方法调用时，可以选择触发相应的事件，这样就能够让所有注册的 LifecycleListener 收到事件。
 *
 * 所谓的收到事件，实际上是调用了 LifecycleListener 接口的 lifecycleEvent 方法，同时传入了封装的事件。
 * LifecycleSupport 是用来辅助这个过程的，实际上就是封装 Event 对象，遍历 LifecycleListener 并进行逐一调用。
 */
public class SimpleContext implements Lifecycle {
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);
    protected boolean started = false;
    protected Lifecycle[] children;

    @Override
    public synchronized void start() throws LifecycleException {
        if (started) {
            throw new LifecycleException("SimpleContext has already started");
        }

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
        started = true;
        try {
            // Start our child containers, if any
            for (int i = 0; i < children.length; i++) {
                children[i].start();
            }
            // Notify our interested LifecycleListeners
            lifecycle.fireLifecycleEvent(START_EVENT, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
    }

    @Override
    public void stop() throws LifecycleException {
        if (!started) {
            throw new LifecycleException("SimpleContext has not been started");
        }
        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;
        try {
            // Start our child containers, if any
            for (int i = 0; i < children.length; i++) {
                children[i].stop();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
    }


    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }

    @Override
    public void init() throws LifecycleException {

    }

    @Override
    public void destroy() throws LifecycleException {

    }

    @Override
    public LifecycleState getState() {
        return null;
    }
}
