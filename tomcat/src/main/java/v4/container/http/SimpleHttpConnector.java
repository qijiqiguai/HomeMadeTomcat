package v4.container.http;

import org.apache.catalina.Container;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author wangqi
 * @date 2017/10/9
 */
public class SimpleHttpConnector implements Runnable {
    boolean stopped = false;
    private String scheme = "http";
    SimpleHttpProcessor processor;

    public SimpleHttpConnector(Container container) {
        processor = new SimpleHttpProcessor(container);
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        int port = 8989;
        String ip = "127.0.0.1";
        try{
            //基础还是基于Socket的编程
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip));
            System.out.println("Init SocketServer @ " + ip + ":" + port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!stopped) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                processor.process(socket);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public String getScheme(){
        return scheme;
    }

}
