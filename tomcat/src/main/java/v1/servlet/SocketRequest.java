package v1.servlet;

import util.StreamUtil;
import java.io.InputStream;

/**
 * Created by wangqi on 2017/9/2 下午10:47.
 */
public class SocketRequest {
    private InputStream inputStream;
    private String uri;

    public SocketRequest(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void parse(){
        String res = StreamUtil.inputStream2String(inputStream);
        System.out.println(res);

        // GET /index.html HTTP/1.1 的形式
        if( null!=res && res.contains(" ")){
            this.uri = res.split(" ")[1];
        }
    }

    public String getUri() {
        return uri;
    }
}
