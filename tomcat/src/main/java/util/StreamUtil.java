package util;

import java.io.*;

/**
 * Created by wangqi on 2017/9/2 下午10:56.
 */
public class StreamUtil {
    public static String inputStream2String(InputStream is) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
