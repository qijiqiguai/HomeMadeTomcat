package util;

/**
 * Created by wangqi on 2017/9/28 下午8:15.
 */

import org.apache.catalina.util.DateTool;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.FieldPosition;
import java.util.Date;
import java.util.Map;

/**
 * 如果不按照这个标准格式输出，前端是会解析出错的
 * 一定要用HTTP标准格式返回，否则浏览器无法做解析，从而出现请求失败
 * http://blog.csdn.net/liuwenjie517333813/article/details/68060914
 * @author wangqi
 */
public class HttpUtil {

    public static String getCookieHeaderValue(Cookie cookie) {
        StringBuffer buf = new StringBuffer();
        int version = cookie.getVersion();

        // this part is the same for all cookies

        String name = cookie.getName();     // Avoid NPE on malformed cookies
        if (name == null) {
            name = "";
        }
        String value = cookie.getValue();
        if (value == null) {
            value = "";
        }

        buf.append(name);
        buf.append("=");
        maybeQuote(version, buf, value);

        // add version 1 specific information
        if (version == 1) {
            // Version=1 ... required
            buf.append (";Version=1");

            // Comment=comment
            if (cookie.getComment() != null) {
                buf.append (";Comment=");
                maybeQuote (version, buf, cookie.getComment());
            }
        }

        // add domain information, if present

        if (cookie.getDomain() != null) {
            buf.append(";Domain=");
            maybeQuote (version, buf, cookie.getDomain());
        }

        // Max-Age=secs/Discard ... or use old "Expires" format
        if (cookie.getMaxAge() >= 0) {
            if (version == 0) {
                buf.append (";Expires=");
                if (cookie.getMaxAge() == 0) {
                    DateTool.oldCookieFormat.get().format(new Date(10000), buf, new FieldPosition(0));
                } else {
                    DateTool.oldCookieFormat.get().format(
                            new Date(System.currentTimeMillis() + cookie.getMaxAge() * 1000L), buf, new FieldPosition(0)
                    );
                }
            } else {
                buf.append (";Max-Age=");
                buf.append (cookie.getMaxAge());
            }
        } else if (version == 1) {
            buf.append(";Discard");
        }

        // Path=path
        if (cookie.getPath() != null) {
            buf.append (";Path=");
            maybeQuote (version, buf, cookie.getPath());
        }

        // Secure
        if (cookie.getSecure()) {
            buf.append (";Secure");
        }

        return buf.toString();
    }

    private static void maybeQuote (int version, StringBuffer buf, String value) {
        if (version == 0 || isToken (value)) {
            buf.append(value);
        } else {
            buf.append ('"');
            buf.append (value);
            buf.append ('"');
        }
    }
    private static final String tspecials = "()<>@,;:\\\"/[]?={} \t";
    private static boolean isToken (String value) {
        int len = value.length ();

        for (int i = 0; i < len; i++) {
            char c = value.charAt (i);
            if (c < 0x20 || c >= 0x7f || tspecials.indexOf (c) != -1) {
                return false;
            }
        }
        return true;
    }

    public static String getCookieHeaderName(Cookie cookie) {
        int version = cookie.getVersion();

        if (version == 1) {
            return "Set-Cookie2";
        } else {
            return "Set-Cookie";
        }
    }

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

    public static String msgWrapper(int code, String message, String input) {
        return httpMsgWrapper(code + " " + message, input);
    }

    private static String httpMsgWrapper(String statusText, String message) {
        String info = "HTTP/1.1 " + statusText + " \r\n" +
                "Content-type: text/html \r\n" +
                "Content-Length: " + message.length() + "\r\n" +
                "\r\n" + message;
        return info;
    }

}
