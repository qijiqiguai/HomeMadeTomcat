package v3.connector;

import util.FileUtil;
import util.HttpUtil;
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
    public void process(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            String fileContent = FileUtil.readFileContent( request.getRequestURI() );
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
