package v4.container;


import org.apache.catalina.Loader;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import v4.container.http.SimpleHttpConnector;
import v4.container.valve.CurrentTimeLogValve;
import v4.container.valve.RequestHeaderLogValve;

import java.io.IOException;

/**
 *
 * @author wangqi
 * @date 2017/10/24 下午10:15
 */
public class ContextBootstrap {
    public static void main(String[] args) {
        Loader loader = new SimpleLoader();
        Wrapper wrapper = new SimpleWrapper();
        wrapper.setServletClass("HelloServlet");
        wrapper.setLoader(loader);

        Valve timeVal = new CurrentTimeLogValve();
        Valve headerVal = new RequestHeaderLogValve();
        wrapper.getPipeline().addValve(timeVal);
        wrapper.getPipeline().addValve(headerVal);

        SimpleMapper mapper = new SimpleMapper();
        mapper.addMap("HelloServlet", wrapper);
        SimpleContext context = new SimpleContext(mapper);

        SimpleHttpConnector connector = new SimpleHttpConnector(context);
        connector.start();

        // 驻留主线程
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
