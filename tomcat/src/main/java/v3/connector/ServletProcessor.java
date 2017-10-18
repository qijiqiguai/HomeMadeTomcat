package v3.connector;

import util.FileUtil;
import util.Util;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public static final String SERVLET_BASE = "v3.connector.servlets.";

    public void process(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        String servletName = uri.substring( uri.lastIndexOf("/") + 1 );

        try {
            Class clazz = loadClass(SERVLET_BASE + servletName);
            Servlet servlet = (Servlet) clazz.newInstance();
            servlet.service( request, response );
        } catch (Exception e) {
            try {
                response.sendError(500, Util.getExceptionString(e));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private Class loadClass(String name) throws ClassNotFoundException, MalformedURLException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL repository = classLoader.getResource(""); // 当前Classloader的根目录
        URLClassLoader loader = new URLClassLoader( new URL[] { repository } );

        Class clazz = loader.loadClass(name);
        return clazz;
    }
}
