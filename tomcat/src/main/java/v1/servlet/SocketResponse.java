package v1.servlet;

import util.FileUtil;

import java.io.IOException;
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

    public void sendStaticFile() throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));//包装后的输出缓冲字符流
        String fileContent = FileUtil.readFileContent(request.getUri());
        String info;
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
        writer.println(info);//如果服务器不打算对客户端发送.ico结尾的文件，可以这一写，然后直接返回，跳过发送该文件的过程
        writer.close();
        outputStream.close();
    }
}
