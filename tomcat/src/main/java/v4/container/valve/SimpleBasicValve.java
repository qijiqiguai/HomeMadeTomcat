package v4.container.valve;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Valve;
import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import v4.container.SimpleWrapper;
import v4.container.http.InvokePatch;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author wangqi
 * @date 2017/10/18 下午7:59
 */
public final class SimpleBasicValve implements Valve, Contained, InvokePatch {
    protected Container container;
    protected static final String info = "SimpleBasicValve";

    public SimpleBasicValve(Container container) {
        this.container = container;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if( container instanceof SimpleWrapper){
            SimpleWrapper sw = (SimpleWrapper) container;
            Servlet servlet = sw.allocate();
            servlet.service(request, response);
        }
    }

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if( container instanceof SimpleWrapper){
            SimpleWrapper sw = (SimpleWrapper) container;
            Servlet servlet = sw.allocate();
            servlet.service(request, response);
        }
    }

    @Override
    public void event(Request request, Response response, CometEvent event) throws IOException, ServletException {
        // 事件处理
    }

    @Override
    public void backgroundProcess() {
        // 后台任务
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public Valve getNext() {
        // Do Nothing, 基础Valve没有下一步操作
        return null;
    }

    @Override
    public void setNext(Valve valve) {
        // Do Nothing, 基础Valve没有下一步操作
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
}
