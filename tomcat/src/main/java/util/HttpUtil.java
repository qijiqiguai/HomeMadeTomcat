package util;

/**
 * Created by wangqi on 2017/9/28 下午8:15.
 */

/**
 * 如果不按照这个标准格式输出，前端是会解析出错的
 * 一定要用HTTP标准格式返回，否则浏览器无法做解析，从而出现请求失败
 * http://blog.csdn.net/liuwenjie517333813/article/details/68060914
 */
public class HttpUtil {

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
