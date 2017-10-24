package v4.container;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import v4.container.http.InvokePatch;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author wangqi
 * @date 2017/10/18 下午7:59
 */
public class SimplePipeline implements Pipeline, Contained, InvokePatch {
    Valve basicValve;
    ArrayList<Valve> valves = new ArrayList<>();
    Container container;

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        InvokePatch first = (InvokePatch)this.getFirst();
        if( null == first ){
            ((InvokePatch)basicValve).invoke(request, response);
        }else {
            first.invoke(request, response);
        }
    }

    public SimplePipeline(Container container) {
        this.container = container;
    }

    @Override
    public Valve getBasic() {
        return this.basicValve;
    }

    @Override
    public void setBasic(Valve valve) {
        this.basicValve = valve;
    }

    @Override
    public void addValve(Valve valve) {
        // 需要设置Next
        this.valves.add(valve);
        int size = valves.size();
        if( size >= 2 ){
            valves.get(size-2).setNext(valves.get(size-1));
        }
        valves.get(size-1).setNext(basicValve);
    }

    @Override
    public Valve[] getValves() {
        return valves.toArray(new Valve[valves.size()]);
    }

    @Override
    public void removeValve(Valve valve) {
        // 这个过程效率不高，实际上应该直接从List里面断开修改Next，但是懒得测试了
        ArrayList<Valve> old = this.valves;
        this.valves = new ArrayList<>();
        old.forEach( v -> {
            if( !v.equals(valve) ){
                this.addValve(v);
            }
        } );
    }

    @Override
    public Valve getFirst() {
        return valves.size()>0 ? valves.get(0) : null;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
}
