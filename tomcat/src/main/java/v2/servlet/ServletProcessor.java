package v2.servlet;

import util.FileUtil;
import javax.servlet.Servlet;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by wangqi on 2017/9/26 下午7:41.
 */

/**
 * 跟SocketServer最大的不同是，根据字符串动态加载Servlet类并调用特定的接口方法来完成处理
 * 而不是根据字符串找到特定方法来完成处理。
 */
public class ServletProcessor {
    public static final String SERVLET_BASE = "v2.servlet.servlets.";

    public void process(ServletRequestImpl request, ServletResponseImpl response) {
        String uri = request.getUri();
        String servletName = uri.substring( uri.lastIndexOf("/") + 1 );

        try {
            Class clazz = loadClass(SERVLET_BASE + servletName);
            Servlet servlet = (Servlet) clazz.newInstance();

            // ServletRequestImpl 中有一些自有方法不是 Servlet API 标准的，那么在 Servlet 实例中应该是不可以访问的。
            // 利用门面类屏蔽非标准API
            RequestFacade req = new RequestFacade(request);
            ResponseFacade res = new ResponseFacade(response);
            servlet.service( req, res );
        } catch (Exception e) {
            e.printStackTrace();
            ServletResponseImpl.sendError(response.getOriginalOutputStream(), e);
        }
    }

    // 这个过程可以单独测试
    private Class loadClass(String name) throws ClassNotFoundException, MalformedURLException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL repository = classLoader.getResource(""); // 当前Classloader的根目录
        URLClassLoader loader = new URLClassLoader( new URL[] { repository } );

        Class clazz = loader.loadClass(name);
        return clazz;
    }
}
