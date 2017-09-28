package v2.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Created by wangqi on 2017/9/28 下午8:51.
 */
public class ResponseFacade implements ServletResponse {
    private ServletResponseImpl res;

    public ResponseFacade(ServletResponseImpl response) {
        this.res = response;
    }

    @Override
    public String getCharacterEncoding() {
        return res.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return res.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return res.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return res.getWriter();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        res.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
        res.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        res.setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type) {
        res.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
        res.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return res.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        res.flushBuffer();
    }

    @Override
    public void resetBuffer() {
        res.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return res.isCommitted();
    }

    @Override
    public void reset() {
        res.reset();
    }

    @Override
    public void setLocale(Locale loc) {
        res.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        return res.getLocale();
    }
}
