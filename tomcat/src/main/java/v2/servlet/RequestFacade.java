package v2.servlet;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wangqi on 2017/9/28 下午8:42.
 */

/**
 * ServletRequestImpl 中有一些自有方法不是 Servlet API 标准的，那么在 Servlet 实例中应该是不可以访问的。
 * 解决方法有两种
 *  1：访问控制符，限制 ServletRequestImpl 私有方法只能在当前包中访问，这可能会影响包划分的自由度
 *  2：利用一个门面类，将 Servlet API 中标准的方法提供出去，而不将 ServletRequestImpl 实例传给 Servlet。
 */
public class RequestFacade implements ServletRequest{
    private ServletRequestImpl req;

    public RequestFacade(ServletRequestImpl request) {
        this.req = request;
    }

// Standard Servlet API
    @Override
    public Object getAttribute(String name) {
        return req.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return req.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return req.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        req.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {
        return req.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return req.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return req.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return req.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        return req.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return req.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return req.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return req.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return req.getProtocol();
    }

    @Override
    public String getScheme() {
        return req.getScheme();
    }

    @Override
    public String getServerName() {
        return req.getServerName();
    }

    @Override
    public int getServerPort() {
        return req.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return req.getReader();
    }

    @Override
    public String getRemoteAddr() {
        return req.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return req.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        req.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        req.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return req.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return req.getLocales();
    }

    @Override
    public boolean isSecure() {
        return req.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return req.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return req.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return req.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return req.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return req.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return req.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return req.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return req.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return req.startAsync();
    }

    @Override
    public boolean isAsyncStarted() {
        return req.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return req.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return req.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return req.getDispatcherType();
    }
}
