package v4.container;

import org.apache.catalina.Container;
import org.apache.catalina.connector.Request;
import v4.container.http.Mapper;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wangqi
 * @date 2017/10/24 下午8:02
 */
public class SimpleMapper implements Mapper {
    private Container container;
    private Map<String, Container> mapper;
    private String protocol;

    public SimpleMapper(){
        this.mapper = new HashMap<>();
    }

    public void addMap(String key, Container container){
        this.mapper.put(key, container);
    }

    public Container map(HttpServletRequest request, boolean update) {
        String[] split =  request.getRequestURI().split("/");
        return mapper.get(split[split.length-1]);
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public Container map(Request request, boolean update) {
        return mapper.get(request.getRequestURI());
    }
}
