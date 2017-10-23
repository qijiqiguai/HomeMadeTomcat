package v4.container.http;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wangqi on 2017/10/23 下午8:59.
 *
 * 由于 Tomcat 7.0 中的 Container.invoke 方法用的不是 HttpServletRequest&HttpServletResponse
 * 导致 V3 中的Processor 和 HttpRequest&HttpResponse 无法在 V4 中复用。所以创建一个补丁接口，实现兼容。
 */
public interface InvokePatch {

    void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

}
