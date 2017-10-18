package v3.connector;

import org.apache.catalina.util.Enumerator;
import org.apache.catalina.util.ParameterMap;
import util.Util;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.util.*;

/**
 * Created by wangqi on 2017/10/11 下午6:50.
 */
public class HttpRequest implements HttpServletRequest{
    // 有一些Header，例如Accept-Language，会发送不止一条过来，也可能通过 ; 来分割多个值
    private Map<String, List<String>> headers = new HashMap<>();
    private List<Cookie> cookies = new ArrayList<>();
    private ParameterMap<String, String[]> parameters = null; // 很简单的类，HashMap的子类，主要是增加了locked标记，锁住的时候不能修改
    private boolean parameterParsed = false;

    private String queryString;
    private String method;
    private String protocol;
    private int contentLength;
    private String contentType;
    private String requestContent;
    private String characterEncoding;
    private String requestUri;
    private String requestedSessionId;
    private boolean requestedSessionURL;

    public HttpRequest() {

    }


// 在HttpRequest生成初期是不格式化这些信息的，只有到第一次使用的时候才哥格式化
// 这样如果用不到参数就不解析，有一定概率可以减少CPU使用，这是一种懒加载的方式
    @Override
    public String getParameter(String name) {
        parseParameters();
        String values[] = (String[]) parameters.get(name);
        if (values != null) {
            return (values[0]);
        } else {
            return (null);
        }
    }

    @Override
    public Map getParameterMap() {
        parseParameters();
        return (this.parameters);
    }

    @Override
    public Enumeration getParameterNames() {
        parseParameters();
        return (new Enumerator(parameters.keySet()));
    }

    @Override
    public String[] getParameterValues(String name) {
        parseParameters();
        String[] values = parameters.get(name);
        return values;
    }

    private void parseParameters() {
        if (parameterParsed) {
            return;
        }
        this.parameters = new ParameterMap();
        parameters.setLocked(false);

        String encoding = this.getCharacterEncoding();
        if( null == encoding ){
            encoding = "utf-8";
        }

        String queryString = this.getQueryString();
        if( null != queryString && !"".equals(queryString.trim()) ){
            parseParameterStr(this.parameters, queryString);
        }

        if( contentType == null ){
            contentType = "";
        }
        if("POST".equals(method) && contentType.contains("application/x-www-form-urlencoded") && contentLength>0){
            parseParameterStr(this.parameters, requestContent);
        }

        //最终锁住参数表，不允许修改
        parameterParsed = true;
        parameters.setLocked(true);
    }

    /**
     * 这里没有考虑 encoding & urlEncoding & 多值
     * @param map
     * @param input
     */
    private void parseParameterStr(Map<String, String[]> map, String input) {
        String splitter = "=";
        if (null == input || !input.contains(splitter) ) {
            return;
        }

        String[] kvs = input.split("&");
        if (kvs.length > 0) {
            for(int i=0; i<kvs.length; i++) {
                if( kvs[i].contains(splitter) ){
                    String key = kvs[i].split(splitter)[0].trim();
                    String value = kvs[i].split(splitter)[1].trim();
                    Util.addToMap(this.parameters, key, value);
                }else {
                    Util.addToMap(this.parameters, kvs[i], null);
                }
            }
        }


    }


// ---------- Set & Add 方法 ---------------------------------------------------


    public void addHeader(String name, String value) {
        if(!headers.containsKey(name)){
            headers.put(name, new ArrayList<>());
        }
        if( value.contains(";") ){
            String[] splited = value.split(";");
            for( int i=0; i<splited.length; i++ ){
                this.headers.get(name).add(splited[i]);
            }
        }else {
            this.headers.get(name).add(value);
        }
    }
    public void addCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        this.cookies.add(cookie);
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public void setRequestedSessionURL(boolean requestedSessionURL) {
        this.requestedSessionURL = requestedSessionURL;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    @Override
    public String getAuthType() {
        return null;
    }

    // --------------- 已经实现的方法 ------------------------------------
    @Override
    public Cookie[] getCookies() {
        return this.cookies.size()==0 ? null :
                this.cookies.toArray( new Cookie[this.cookies.size()] );
    }

    // 以长整型的方式拿到某个Header中的时间，比如 If-Modified-Since
    @Override
    public long getDateHeader(String name) {
        //Header是大小写不敏感的
        try {
            String val = headers.get(name.toLowerCase()).get(0);
            return Long.parseLong(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("No such key or can't parse value");
        }
    }

    @Override
    public int getIntHeader(String name) {
        //Header是大小写不敏感的
        try {
            String val = headers.get(name.toLowerCase()).get(0);
            return Integer.parseInt(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("No such key or can't parse value");
        }
    }

    // 如果有多个值，取第一个
    @Override
    public String getHeader(String name) {
        if( headers.get(name)!=null && headers.get(name).size()>0 ){
            return headers.get(name.toLowerCase()).get(0);
        }else {
            return null;
        }

    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> list = headers.get(name.toLowerCase());
        return new Enumerator(list);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> keys = this.headers.keySet();
        return new Enumerator(keys);
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public String getRequestedSessionId() {
        return this.requestedSessionId;
    }

    @Override
    public String getRequestURI() {
        return this.requestUri;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return !this.requestedSessionURL;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return this.requestedSessionURL;
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        this.characterEncoding = env;
    }

    @Override
    public int getContentLength() {
        return this.contentLength;
    }

    @Override
    public long getContentLengthLong() {
        return (long)this.contentLength;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }


    @Override
    public String getProtocol() {
        return this.protocol;
    }


// ---------------- 暂未实现 -------------------------------------
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }


    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

}
