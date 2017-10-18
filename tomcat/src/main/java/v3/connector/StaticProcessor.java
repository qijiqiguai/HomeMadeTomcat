package v3.connector;

import util.FileUtil;
import util.HttpUtil;
import v1.socket.SocketServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author wangqi
 * @date 2017/10/17 下午7:45
 */
public class StaticProcessor {
    public static final String WEB_ROOT = "webroot";

    public void process(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            String uri = "classpath:" + SocketServer.WEB_ROOT + request.getRequestURI();
            String fileContent = FileUtil.readFileContent( uri );
            String info;
            if(null == fileContent ) {
                info = HttpUtil.notFoundWrapper("<h1> File Not Found </h1>");
            }else {
                info = HttpUtil.okWrapper(fileContent);
            }
            writer.println(info);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
