package v4.container.valve;

import org.apache.catalina.Container;
import org.apache.catalina.Valve;
import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wangqi on 2017/10/18 下午8:00.
 */
public class RequestHeaderLogValve implements Valve {
    protected Container container;
    protected Valve next;

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        System.out.println("RequestHeaderLogValve -> " + request.getHeaderNames());

        // 要执行下一个 Valve, 通过Next的方式来执行，比每次在Pipeline中循环执行效率高多了。
        // 因为实际上 Valve 是一个经常会执行（每个接受到的请求都需要执行）， 但是极少改变的结构，所以通过这个方式来优化效率。
        if( null != next ){
            next.invoke(request, response);
        }
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public Valve getNext() {
        return this.next;
    }

    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }

    @Override
    public void backgroundProcess() {

    }

    @Override
    public void event(Request request, Response response, CometEvent event) throws IOException, ServletException {

    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }
}
