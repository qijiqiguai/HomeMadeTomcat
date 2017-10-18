package util;

/**
 * Created by wangqi on 2017/9/28 下午8:15.
 */

import org.apache.catalina.util.DateTool;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.text.FieldPosition;
import java.util.Date;

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
     *
     * TODO, 无法处理 Keep-Alive
     */
    public static String httpRequestToString(InputStream in) throws IOException {
        int bufferSize = 4096;
        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer();
        byte[] buffer = new byte[bufferSize];
        int len = in.read(buffer);
        while (len != -1){
            //读出来多少字节，就往Buffer中写多少字节
            for (int j=0; j<len; j++) {
                request.append((char) buffer[j]);
            }
            if( len < bufferSize ){
                len = -1;
            }else {
                // 在 HTTP/1.1 之后具有 Keep-Alive 能力并且是默认开启的，则 Socket.InputStream 是持续不关闭的
                // 在这种情况下，in.read(buffer) 将持续不返还，直到客户端主动切断或者 timeout。
                // 此时如果 请求传输来的字节数恰好是 bufferSize 的整数倍时，再调用 in.read 会由于持续读不到并且也没有关闭 Steam, 从而导致阻塞。
                // 增加了 available 判断之后，不会在没有输入的情况下走到 in.read，从而规避了阻塞。
                // 但是 Keep-Alive 本身就是要等后续的输入的，这里相当于没有等直接返回了，所以不能处理 Keep-Alive 的请求。
                if( in.available() > 0 ){
                    len = in.read(buffer);
                }else {
                    len = -1;
                }
            }
        }
        return request.toString();
    }

// Http Response Wrapper
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
