package v4.container.http;


import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import util.HttpUtil;
import util.Util;
import v3.connector.HttpRequest;
import v3.connector.HttpResponse;
import v3.connector.StaticProcessor;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wangqi on 2017/10/9.
 *
 * 对于Request的处理是基于HTTP协议来完成的，标准的HTTP协议包括了：
 * 第一部分：请求信息，例如 GET /index.html HTTP/1.1
 * 第一行的信息分成三个部分，第一部分为方法类型，第二部分是请求的目标，第三部分是协议版本。行结尾为CRLF，即\r\n。
 * 第二部分：请求头，例如 cookie, content-type, content-length等。每一个头信息为一行，以CRLF结尾。
 * 第三部分：空行，只有一个CRLF。
 * 第四部分，对于POST方法存在，为POST方法体。
 *
 * 典型的样例：

 POST /index.html HTTP/1.1
 HOST: 127.0.0.1:8080
 accept: application/json
 accept-encoding: gzip
 connection: Keep-Alive
 content-length: 2
 content-type: application/json
 cookie: _ga=GA1.2.437127923.1502421211; _gid=GA1.2.1122052034.1502421211
 pragma: no-cache
 user-agent: okhttp/3.4.1

 {json: true}

 * 这里只是为了功能，大量使用了字符串切分，这实际上不是最高效的方法。Tomcat中使用的是字符数组查找和子串来提高效率。
 * 同时，也有很多可能出现的异常没有处理，比如各种Header的内容检查、各种Header中标准的特殊字符的处理（;=:等符号）。
 * 这里也感叹一下，严谨的工业级产品对于细节的处理确实不是简单的自己随便写写可以达到的。
 *
 * 同时对于 Http1.1 开始支持的 Keep-Alive 特性也没有处理，这一部分不影响整体流程的理解。
 *
 * @author wangqi
 */
public class SimpleHttpProcessor {
    HttpRequest request;
    HttpResponse response;
    Container container;

    public SimpleHttpProcessor(Container container){
        this.container = container;
    }

    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();

            request = new HttpRequest();
            String requestStr = HttpUtil.httpRequestToString(input);
            parseRequest(requestStr);
            parseHeader(requestStr);

            response = new HttpResponse(output, request);
            response.addHeader("Server", "HomeMadeTomcat");

            if( request.getRequestURI().startsWith("/servlet/") ){
                ((InvokePatch)container).invoke(request, response);
            }else {
                StaticProcessor processor = new StaticProcessor();
                processor.process(request, response);
            }
        }catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(500, Util.getExceptionString(e));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }finally {
            if(socket != null){
                try {
                    socket.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void parseHeader(String requestStr) {
        // 根据HTTP协议，将请求体分离出去
        String[] requestSplit = requestStr.split("\r\n\r\n");

        // 如果存在，则写入请求体
        if( requestSplit.length > 1 ){
            request.setRequestContent(requestSplit[1]);
        }

        String[] headers = requestSplit[0].split("\r\n");
        if( headers.length > 1 ) { // 不止一行的时候，证明有Header存在
            // 从第一行开始，第0行是请求信息不属于Header
            for( int i=1; i<headers.length; i++ ){
                String one = headers[i].trim();
                if(one.equals("")){
                    continue;
                }
                int index = one.indexOf(":"); //由于Header键值对中可能出现值中包含冒号的情况，所以这里不能用 split
                String name = one.substring(0, index).trim().toLowerCase(); //Header是大小写不敏感的
                String value = one.substring(index+1).trim();
                request.addHeader(name, value);

                if(name.equals("cookie")){
                    parseCookie(value);
                }else if(name.equals("content-length")){
                    try {
                        request.setContentLength(Integer.parseInt(value));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Content-Length should be integer");
                    }
                }else if(name.equals("content-type")){
                    request.setContentType(value);
                }
            }
        }
    }

    // Cookie的格式 key=val; key1=val1
    private void parseCookie(String cookieStr) {
        String[] cookies = cookieStr.split(";");
        if(null!=cookies && cookies.length > 0){
            for( int i=0; i<cookies.length; i++ ){
                String one = cookies[i];
                int index = one.indexOf("=");
                if( index < 0){
                    throw new IllegalArgumentException("Cookie should be K-V pair");
                }
                String name = one.substring(0, index).trim();
                String value = one.substring(index+1).trim();
                request.addCookie(name, value);

                if(name.equals("jsessionid")){
                    // 不是从URL中获取SessionID
                    request.setRequestedSessionURL(false);
                    request.setRequestedSessionId(value);
                }
            }
        }
    }

    // 处理请求的第一行，即 METHOD /xxx HTTP/1.1
    private void parseRequest(String requestStr) throws IOException, ServletException {
        // 校验首行格式， 可以更加完善
        String[] requestLine = requestStr.split("\n")[0].split(" ");
        if( requestLine.length < 3 ){
            throw new IllegalArgumentException("Incomplete Http RequestLine: " + requestStr.split("\n")[0]);
        }
        request.setMethod(requestLine[0]);
        request.setProtocol(requestLine[2]);

        // 获取请求参数，并设置到 HttpRequest 上
        String requestFullUri = requestLine[1];
        String uri;
        if(requestFullUri.contains("?")){
            String[] splited = requestFullUri.split("\\?");
            this.request.setQueryString(splited[1]);
            uri = splited[0];
        }else {
            this.request.setQueryString(null);
            uri = requestFullUri;
        }

        // 确定是否是绝对URI，即带有 http:// 或者 https://
        if( !uri.startsWith("/") ){
            uri = uri.replaceAll("https+://", "");
        }

        // jsessionid 处理，这个值可以改成可配置的, jsessionid是放到问号前面的
        // http://localhost:8080/aaa/bbb.jsp;jsessionid=xxx?para=1
        if( uri.contains(";jsessionid=") ){
            request.setRequestedSessionURL(true);
            String[] splited = uri.split(";jsessionid=");
            request.setRequestedSessionId(splited[1]);
            uri = splited[0];
        }else{
            request.setRequestedSessionURL(false);
            request.setRequestedSessionId(null);
        }

        // 标准化URL，将其中的转意字符改掉，. 和 .. 等符号做处理，这里是个简化版本
        String normalizedUri = normalize(uri);
        if(null != normalizedUri){
            request.setRequestUri(normalizedUri);
        }else{
            request.setRequestUri(uri);
            throw new ServletException("Invalid URI:" + uri);
        }
    }

    /**
     * Return a context-relative path, beginning with a "/", that represents
     * the canonical version of the specified path after ".." and "." elements
     * are resolved out.  If the specified path attempts to go outside the
     * boundaries of the current context (i.e. too many ".." path elements
     * are present), return <code>null</code> instead.
     *
     * @param path Path to be normalized
     */
    protected String normalize(String path) {
        if (path == null) {
            return null;
        }
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e")) {
            normalized = "/~" + normalized.substring(4);
        }

        // Prevent encoding '%', '/', '.' and '\', which are special reserved
        // characters
        if ((normalized.indexOf("%25") >= 0)
                || (normalized.indexOf("%2F") >= 0)
                || (normalized.indexOf("%2E") >= 0)
                || (normalized.indexOf("%5C") >= 0)
                || (normalized.indexOf("%2f") >= 0)
                || (normalized.indexOf("%2e") >= 0)
                || (normalized.indexOf("%5c") >= 0)) {
            return null;
        }

        if (normalized.equals("/.")) {
            return "/";
        }

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0) {
                break;
            }
            if (index == 0) {
                return (null);  // Trying to go outside our context
            }
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
                    normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.indexOf("/...") >= 0) {
            return (null);
        }

        // Return the normalized path that we have completed
        return (normalized);

    }
}
