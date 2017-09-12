package v1.servlet;

import util.FileUtil;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by wangqi on 2017/9/2 下午10:47.
 */
public class SocketResponse {
    private OutputStream outputStream;
    private SocketRequest request;

    public SocketResponse(OutputStream outputStream, SocketRequest request) {
        this.outputStream = outputStream;
        this.request = request;
    }

    public void sendStaticFile() {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));//包装后的输出缓冲字符流
        String fileContent = FileUtil.readFileContent( request.getUri() );
        String info;
        // *** 一定要用HTTP标准格式返回，否则浏览器无法做解析，从而出现请求失败
        // http://blog.csdn.net/liuwenjie517333813/article/details/68060914
        if(null == fileContent ) {
            info = "HTTP/1.1 404 File Not Found \r\n" +
                   "Content-type: text/html \r\n" +
                   "Content-Length: 23\r\n" +
                   "\r\n" +
                   "<h1> File Not Found </h1>";
        }else {
            info = "HTTP/1.1 200 OK \r\n" +
                   "Content-type: text/html \r\n" +
                   "Content-Length: " + fileContent.length() + "\r\n" +
                   "\r\n" +
                   fileContent;
        }
        writer.println(info);
        writer.close();
    }

    public static void sendError(OutputStream outputStream, Exception e) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));//包装后的输出缓冲字符流
        String info = "HTTP/1.1 404 File Not Found \r\n" +
                    "Content-type: text/html \r\n" +
                    "Content-Length: 23\r\n" +
                    "\r\n" +
                    "<h1> Unexpected Exception </h1></br>" +
                    e.getMessage();
        writer.println(info);
        writer.close();
    }
}
