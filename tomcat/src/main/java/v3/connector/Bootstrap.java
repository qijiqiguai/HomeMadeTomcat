package v3.connector;

/**
 * Created by wangqi on 2017/10/11 下午6:59.
 */
public class Bootstrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
