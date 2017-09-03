package v1.servlet;

import util.FileUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wangqi on 2017/9/2 下午10:47.
 */
public class SocketResponse {
    private OutputStream outputStream;
    private SocketRequest request;

    public SocketResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setRequest(SocketRequest request) {
        this.request = request;
    }

    public void sendStaticFile() throws IOException {
        String fileContent = FileUtil.readFile(request.getUri());
        if(null == fileContent ){
            String error = " HTTP/1.1 404 File Not Found ya \r\n" +
                    "Content-type: text/html \r\n" +
                    "Content-Length: 23\r\n" +
                    "\r\n" +
                    "<h1> File Not Found ya </h1>";
            outputStream.write(error.getBytes());
        }else {
            outputStream.write(fileContent.getBytes());
        }
    }
}
