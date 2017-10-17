package v3.connector;

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
public class HttpConnector implements Runnable {
    boolean stopped = false;
    private String scheme = "http";

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        int port = 8080;
        String ip = "127.0.0.1";
        try{
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip)); //基础还是基于Socket的编程
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
                // 连接器就是连接，不具体做处理
                HttpProcessor processor = new HttpProcessor();
                // 如果 process 处理过程是同步的，那么只有等待处理返回之后，while 才会继续，所以这会大大影响连接效率
                // 可以将 process 改成异步的
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
