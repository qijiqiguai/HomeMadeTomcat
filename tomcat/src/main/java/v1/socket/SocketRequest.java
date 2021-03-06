package v1.socket;

import util.HttpUtil;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangqi on 2017/9/2 下午10:47.
 */
public class SocketRequest {
    InputStream inputStream;
    String uri;
    String fullReq;

    public SocketRequest(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public SocketRequest parse() throws IOException {
        fullReq = HttpUtil.httpRequestToString(inputStream);
//        System.out.println(fullReq);

        // GET /index.html HTTP/1.1 的形式
        if( null!=fullReq && !"".equals(fullReq) && fullReq.contains(" ")){
            String uri = fullReq.split(" ")[1];
            this.uri = "classpath:" + SocketServer.WEB_ROOT + uri;
        }
        return this;
    }

    public String getUri() {
        return uri;
    }

    public String getFullReq(){
        return fullReq;
    }
}
