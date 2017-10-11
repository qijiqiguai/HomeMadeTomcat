package v2.servlet.servlets;

import util.HttpUtil;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wangqi on 2017/9/26 下午6:59.
 */
public class HelloServlet implements Servlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("Servlet Init");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        // 注：这里拿到的应该就是标准的 ServletRequest & ServletResponse，而不应该包含其他自定义接口，否则就会出现混乱
        // 因为 Servlet 开发者其实是应用开发者，可能不了解底层，如果混了其他非标准接口，可能导致误用
        // 所以提供了 RequestFacade & ResponseFacade
        System.out.println("InService");
        PrintWriter writer = servletResponse.getWriter();

        String message = HttpUtil.okWrapper("Hello Servlet");

        writer.println(message);
        writer.close();
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
        System.out.println("Servlet Destroy");
    }
}
