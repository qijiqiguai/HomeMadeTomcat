package util;

/**
 * Created by wangqi on 2017/9/28 下午8:32.
 */
public class Util {
    public static String getExceptionString(Exception e){
        StackTraceElement[] res = e.getStackTrace();
        StringBuilder sb = new StringBuilder( e.toString() + "/n");
        for(int i=0; i<res.length; i++ ) {
            sb.append(res[i].toString() + "/n");
        }
        return sb.toString();
    }
}
