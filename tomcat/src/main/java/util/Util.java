package util;

import java.util.Map;

/**
 * Created by wangqi on 2017/9/28 下午8:32.
 * @author wangqi
 */
public class Util {

    public static void addToMap(Map<String, String[]> map, String name, String value) {
        String[] newValues = null;
        String[] oldValues = map.get(name);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

    public static String getExceptionString(Exception e){
        StackTraceElement[] res = e.getStackTrace();
        StringBuilder sb = new StringBuilder( e.toString() + "/n");
        for(int i=0; i<res.length; i++ ) {
            sb.append(res[i].toString() + "/n");
        }
        return sb.toString();
    }
}
