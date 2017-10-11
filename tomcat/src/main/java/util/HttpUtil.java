package util;

/**
 * Created by wangqi on 2017/9/28 下午8:15.
 */

import java.io.IOException;
import java.io.InputStream;

/**
 * 如果不按照这个标准格式输出，前端是会解析出错的
 * 一定要用HTTP标准格式返回，否则浏览器无法做解析，从而出现请求失败
 * http://blog.csdn.net/liuwenjie517333813/article/details/68060914
 */
public class HttpUtil {
    /**
     * https://stackoverflow.com/questions/33972296/why-my-server-socket-hangs-on-reading-the-body-of-http-post-request
     * https://stackoverflow.com/questions/11980255/reading-request-content-from-java-socket-inputstream-always-hangs-after-header
     * http://blog.csdn.net/liuwenjie517333813/article/details/68060914
     */
    public static String httpRequestToString(InputStream in) throws IOException {
        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = in.read(buffer); //从Stream中读出来多少个字节
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j=0; j<i; j++) { //读出来多少字节，就往Buffer中写多少字节
            request.append((char) buffer[j]);
        }
        return request.toString();
    }

    public static String okWrapper(String input) {
        return httpMsgWrapper("200 OK", input);
    }

    public static String notFoundWrapper(String input) {
        return httpMsgWrapper("404 File Not Found", input);
    }

    public static String errorWrapper(String input) {
        return httpMsgWrapper("500 Internal Server Error", input);
    }

    private static String httpMsgWrapper(String statusText, String message) {
        String info = "HTTP/1.1 " + statusText + " \r\n" +
                "Content-type: text/html \r\n" +
                "Content-Length: " + message.length() + "\r\n" +
                "\r\n" + message;
        return info;
    }

}
