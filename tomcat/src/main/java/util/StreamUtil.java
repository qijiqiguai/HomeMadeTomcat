package util;

import java.io.*;

/**
 * Created by wangqi on 2017/9/2 下午10:56.
 */
public class StreamUtil {
    /**
     * https://stackoverflow.com/questions/33972296/why-my-server-socket-hangs-on-reading-the-body-of-http-post-request
     * https://stackoverflow.com/questions/11980255/reading-request-content-from-java-socket-inputstream-always-hangs-after-header
     * http://blog.csdn.net/liuwenjie517333813/article/details/68060914
     * 根据Http协议，Request不是以EOF结束的，所以跟常规的文件InputStream的判断结束标准不一样
     */
    public static String httpRequestToString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ( true ) {
            line = reader.readLine();
            sb.append(line);
            if(line.equals("")){ // 不是EOF，如果通过检查 line!=null 来确认是否结束了，那么会陷入死循环直到 客户端超时
                break;
            }
        }
        return sb.toString();
    }
}
