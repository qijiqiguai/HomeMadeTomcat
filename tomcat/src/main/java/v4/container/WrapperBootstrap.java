package v4.container;


import org.apache.catalina.*;
import v4.container.http.SimpleHttpConnector;
import v4.container.valve.CurrentTimeLogValve;
import v4.container.valve.RequestHeaderLogValve;
import java.io.IOException;

/**
 *
 * @author wangqi
 * @date 2017/10/18 下午10:15
 */
public class WrapperBootstrap {
    public static void main(String[] args) {
        SimpleHttpConnector connector = new SimpleHttpConnector();

        Loader loader = new SimpleLoader();
        Wrapper wrapper = new SimpleWrapper();
        wrapper.setServletClass("HelloServlet");
        wrapper.setLoader(loader);
        connector.setContainer(wrapper);

        Valve timeVal = new CurrentTimeLogValve();
        Valve headerVal = new RequestHeaderLogValve();
        wrapper.getPipeline().addValve(timeVal);
        wrapper.getPipeline().addValve(headerVal);

        connector.start();

        // 驻留主线程
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
