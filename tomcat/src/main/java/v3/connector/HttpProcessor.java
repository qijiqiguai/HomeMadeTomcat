package v3.connector;


import util.HttpUtil;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wangqi on 2017/10/9.
 */
public class  HttpProcessor {
    HttpRequest request;

    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();

            request = new HttpRequest(input);
            String requestStr = HttpUtil.httpRequestToString(input);
            parseRequest(requestStr);
            // 暂时不解析请求头，下一个版本再说


        }catch (Exception e) {
            e.printStackTrace();
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
            request.setRequestURI(normalizedUri);
        }else{
            request.setRequestURI(uri);
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
        if (path == null)
            return null;
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
            normalized = "/~" + normalized.substring(4);

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

        if (normalized.equals("/."))
            return "/";

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return (null);  // Trying to go outside our context
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
                    normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.indexOf("/...") >= 0)
            return (null);

        // Return the normalized path that we have completed
        return (normalized);

    }
}
