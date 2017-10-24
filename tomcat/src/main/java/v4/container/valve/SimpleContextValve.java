package v4.container.valve;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Valve;
import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import v4.container.SimpleContext;
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
 * @date 2017/10/24 下午8:12
 */
public class SimpleContextValve  implements Valve, Contained, InvokePatch {
    protected Container container;

    public SimpleContextValve(Container container){
        this.container = container;
    }

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Container con = ((SimpleContext)container).map(request);
        if( null!=con && con instanceof SimpleWrapper){
            SimpleWrapper sw = (SimpleWrapper) con;
            Servlet servlet = sw.allocate();
            servlet.service(request, response);
        }else {
            throw new IllegalArgumentException("Can't Find Wrapper: " + request.getRequestURI());
        }
    }

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public Valve getNext() {
        return null;
    }

    @Override
    public void setNext(Valve valve) {

    }

    @Override
    public void backgroundProcess() {

    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {

    }

    @Override
    public void event(Request request, Response response, CometEvent event) throws IOException, ServletException {

    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }
}
