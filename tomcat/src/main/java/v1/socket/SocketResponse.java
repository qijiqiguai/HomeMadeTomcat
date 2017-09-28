package v1.socket;

import util.FileUtil;
import util.HttpUtil;
import util.Util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by wangqi on 2017/9/2 下午10:47.
 */
public class SocketResponse {
    OutputStream outputStream;
    SocketRequest request;

    public SocketResponse(OutputStream outputStream, SocketRequest request) {
        this.outputStream = outputStream;
        this.request = request;
    }

    public void sendStaticFile() {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));//包装后的输出缓冲字符流
        String fileContent = FileUtil.readFileContent( request.getUri() );
        String info;
        if(null == fileContent ) {
            info = HttpUtil.notFoundWrapper("<h1> File Not Found </h1>");
        }else {
            info = HttpUtil.okWrapper(fileContent);
        }
        writer.println(info);
        writer.close();
    }

    public static void sendError(OutputStream outputStream, Exception e) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));//包装后的输出缓冲字符流
        String info = HttpUtil.errorWrapper(
                "<h1> Unexpected Exception </h1></br>"
                        + Util.getExceptionString(e).replaceAll("/n", "<br/>")
        );
        writer.println(info);
        writer.close();
    }


    public OutputStream getOriginalOutputStream() {
        return outputStream;
    }
}
