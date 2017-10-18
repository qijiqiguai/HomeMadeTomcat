package v3.connector;

import util.HttpUtil;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author wangqi
 * @date 2017/10/17 下午6:16
 */
public class HttpResponse implements HttpServletResponse{

    private static final int BUFFER_SIZE = 1024;
    HttpRequest request;
    OutputStream output;
    PrintWriter writer;
    protected byte[] buffer = new byte[BUFFER_SIZE];
    protected int bufferCount = 0;

    protected boolean committed = false;
    protected int contentLength = -1;
    protected String contentType = null;
    protected String encoding = null;
    protected ArrayList<Cookie> cookies = new ArrayList();
    protected HashMap<String, List<String>> headers = new HashMap();
    protected String message = null;
    protected int status = -1;

    protected final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz",Locale.CHINA);


    public HttpResponse(OutputStream output, HttpRequest request) {
        this.output = output;
        this.request = request;
        this.writer = new PrintWriter(output);
    }

    /**
     * call this method to send headers and response to the output
     */
    public void finishResponse(int code, String content) throws IOException {
        this.status = code;
        this.message = codeMsg(code);
        if( null != content ){
            this.contentLength = content.length();
        }
        sendHeaders();

         writer.println(content);
        // Flush and close the appropriate output mechanism
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    /**
     * Send the HTTP response headers, if this has not already occurred.
     */
    protected void sendHeaders() throws IOException {
        if (isCommitted()) {
            return;
        }
        // Prepare a suitable output writer
        OutputStreamWriter osr = null;
        try {
            osr = new OutputStreamWriter(output, getCharacterEncoding());
        }
        catch (UnsupportedEncodingException e) {
            osr = new OutputStreamWriter(output);
        }
        final PrintWriter outputWriter = new PrintWriter(osr);
        // Send the "Status:" header
        outputWriter.print(request.getProtocol());
        outputWriter.print(" ");
        outputWriter.print(status);
        if (message != null) {
            outputWriter.print(" ");
            outputWriter.print(message);
        }
        outputWriter.print("\r\n");
        // Send the content-length and content-type headers (if any)
        if (getContentType() != null) {
            outputWriter.print("Content-Type: " + getContentType() + "\r\n");
        }
        if (contentLength >= 0) {
            outputWriter.print("Content-Length: " + contentLength + "\r\n");
        }
        // Send all specified headers (if any)
        synchronized (headers) {
            Iterator names = headers.keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                ArrayList values = (ArrayList) headers.get(name);
                Iterator items = values.iterator();
                while (items.hasNext()) {
                    String value = (String) items.next();
                    outputWriter.print(name);
                    outputWriter.print(": ");
                    outputWriter.print(value);
                    outputWriter.print("\r\n");
                }
            }
        }
        // Add the session ID cookie if necessary
        HttpSession session = request.getSession();
        if ((session != null) && session.isNew() ) {
          Cookie cookie = new Cookie("JSESSIONID", session.getId());
          cookie.setMaxAge(-1);
          addCookie(cookie);
        }
        // Send all specified cookies (if any)
        synchronized (cookies) {
            Iterator items = cookies.iterator();
            while (items.hasNext()) {
                Cookie cookie = (Cookie) items.next();
                outputWriter.print(HttpUtil.getCookieHeaderName(cookie));
                outputWriter.print(": ");
                outputWriter.print(HttpUtil.getCookieHeaderValue(cookie));
                outputWriter.print("\r\n");
            }
        }

        // Send a terminating blank line to mark the end of the headers
        outputWriter.print("\r\n");
        outputWriter.flush();

        committed = true;
    }

// Implemented APIs
    @Override
    public void sendError(int sc, String msg) throws IOException {
        finishResponse(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        finishResponse(sc, "Default Error Message: Unexpected Error!");
    }

    private String codeMsg(int status){
        switch (status) {
            case SC_OK:
                return ("OK");
            case SC_ACCEPTED:
                return ("Accepted");
            case SC_BAD_GATEWAY:
                return ("Bad Gateway");
            case SC_BAD_REQUEST:
                return ("Bad Request");
            case SC_CONFLICT:
                return ("Conflict");
            case SC_CONTINUE:
                return ("Continue");
            case SC_CREATED:
                return ("Created");
            case SC_EXPECTATION_FAILED:
                return ("Expectation Failed");
            case SC_FORBIDDEN:
                return ("Forbidden");
            case SC_GATEWAY_TIMEOUT:
                return ("Gateway Timeout");
            case SC_GONE:
                return ("Gone");
            case SC_HTTP_VERSION_NOT_SUPPORTED:
                return ("HTTP Version Not Supported");
            case SC_INTERNAL_SERVER_ERROR:
                return ("Internal Server Error");
            case SC_LENGTH_REQUIRED:
                return ("Length Required");
            case SC_METHOD_NOT_ALLOWED:
                return ("Method Not Allowed");
            case SC_MOVED_PERMANENTLY:
                return ("Moved Permanently");
            case SC_MOVED_TEMPORARILY:
                return ("Moved Temporarily");
            case SC_MULTIPLE_CHOICES:
                return ("Multiple Choices");
            case SC_NO_CONTENT:
                return ("No Content");
            case SC_NON_AUTHORITATIVE_INFORMATION:
                return ("Non-Authoritative Information");
            case SC_NOT_ACCEPTABLE:
                return ("Not Acceptable");
            case SC_NOT_FOUND:
                return ("Not Found");
            case SC_NOT_IMPLEMENTED:
                return ("Not Implemented");
            case SC_NOT_MODIFIED:
                return ("Not Modified");
            case SC_PARTIAL_CONTENT:
                return ("Partial Content");
            case SC_PAYMENT_REQUIRED:
                return ("Payment Required");
            case SC_PRECONDITION_FAILED:
                return ("Precondition Failed");
            case SC_PROXY_AUTHENTICATION_REQUIRED:
                return ("Proxy Authentication Required");
            case SC_REQUEST_ENTITY_TOO_LARGE:
                return ("Request Entity Too Large");
            case SC_REQUEST_TIMEOUT:
                return ("Request Timeout");
            case SC_REQUEST_URI_TOO_LONG:
                return ("Request URI Too Long");
            case SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                return ("Requested Range Not Satisfiable");
            case SC_RESET_CONTENT:
                return ("Reset Content");
            case SC_SEE_OTHER:
                return ("See Other");
            case SC_SERVICE_UNAVAILABLE:
                return ("Service Unavailable");
            case SC_SWITCHING_PROTOCOLS:
                return ("Switching Protocols");
            case SC_UNAUTHORIZED:
                return ("Unauthorized");
            case SC_UNSUPPORTED_MEDIA_TYPE:
                return ("Unsupported Media Type");
            case SC_USE_PROXY:
                return ("Use Proxy");
            case 207:       // WebDAV
                return ("Multi-Status");
            case 422:       // WebDAV
                return ("Unprocessable Entity");
            case 423:       // WebDAV
                return ("Locked");
            case 507:       // WebDAV
                return ("Insufficient Storage");
            default:
                return ("HTTP Response Status " + status);
        }
    }

    @Override
    public boolean containsHeader(String name) {
        return this.headers.containsKey(name);
    }

    @Override
    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, format.format(new Date(date)));
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, format.format(new Date(date)));
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, value+"");
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, value+"");
    }

    @Override
    public void setHeader(String name, String value) {
        if (isCommitted()) {
            return;
        }
        ArrayList values = new ArrayList();
        values.add(value);
        synchronized (headers) {
            headers.put(name, values);
        }
        String match = name.toLowerCase();
        if (match.equals("content-length")) {
            int contentLength = -1;
            try {
                contentLength = Integer.parseInt(value);
            } catch (NumberFormatException e) {

            }
            if (contentLength >= 0) {
                setContentLength(contentLength);
            }
        } else if (match.equals("content-type")) {
            setContentType(value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        if (isCommitted()) {
            return;
        }
        synchronized (headers) {
            ArrayList values = (ArrayList) headers.get(name);
            if (values == null) {
                values = new ArrayList();
                headers.put(name, values);
            }
            values.add(value);
        }
    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.status = sc;
        this.message = sm;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name).toString();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        if( null == encoding ){
            encoding = "utf-8";
        }
        return this.encoding;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return this.writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.encoding = charset;
    }

    @Override
    public void setContentLength(int len) {
        this.contentLength = len;
    }

    @Override
    public void setContentLengthLong(long len) {
        this.contentLength = (int)len;
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }

    @Override
    public int getBufferSize() {
        return this.bufferCount;
    }

    @Override
    public boolean isCommitted() {
        return this.committed;
    }

    @Override
    public void flushBuffer() throws IOException {
        //committed = true;
        if (bufferCount > 0) {
            try {
                output.write(buffer, 0, bufferCount);
            }
            finally {
                bufferCount = 0;
            }
        }
    }

    @Override
    public void resetBuffer() {
        this.buffer = new byte[BUFFER_SIZE];
    }

// Other API, Getters & Setters

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public void setBufferSize(int size) {

    }
}
