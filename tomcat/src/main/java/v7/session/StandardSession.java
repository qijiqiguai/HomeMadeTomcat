package v7.session;


import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.io.*;
import java.util.Enumeration;


/**
 * @author wangqi
 * @date 2017/10/25 下午6:55
 */
public class StandardSession implements HttpSession, Serializable {
    private long createdTime;
    private long lastAccessTime;
    private String id;

    @Override
    public long getCreationTime() {
        return createdTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessTime;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Deprecated
    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Deprecated
    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Deprecated
    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Deprecated
    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }
}