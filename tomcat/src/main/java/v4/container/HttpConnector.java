package v4.container;

import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Transport;

import java.util.Map;

/**
 * Created by wangqi on 2017/10/18 下午9:06.
 */
public class HttpConnector implements Connector {
    @Override
    public String name() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Transport transport() {
        return null;
    }

    @Override
    public Map<String, Argument> defaultArguments() {
        return null;
    }
}
