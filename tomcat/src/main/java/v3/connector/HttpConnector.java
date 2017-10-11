package v3.connector;

import v2.servlet.ServletProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by wangqi on 2017/10/9.
 */
public class HttpConnector implements Runnable {
    boolean stoped = false;
    private String scheme = "http";

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        ServletProcessor servletProcessor = new ServletProcessor();
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

        while (!stoped) {
            Socket socket = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            try {
                socket = serverSocket.accept();
                HttpProcessor processor = new HttpProcessor();
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
