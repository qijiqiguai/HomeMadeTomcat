package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangqi on 2017/9/2 下午10:56.
 */
public class StreamUtil {
    public static String inputStream2String(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        try {
            while((i=is.read())!=-1){
                baos.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return   baos.toString();
    }
}
