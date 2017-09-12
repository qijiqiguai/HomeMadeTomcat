package v1.servlet;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by wangqi on 2017/9/2 下午10:32.
 */
public class SocketServer {
    public static final String WEB_ROOT = "webroot";
    private static final String SHUTDOWN_CMD = "/SHUTDOWN";
    private static boolean shutdown = false;

    public static void main(String[] args) {
        SocketServer ss = new SocketServer();
        ss.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        String ip = "127.0.0.1";
        try{
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip));
            System.out.println("Init SocketServer @ " + ip + ":" + port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!shutdown) {
            Socket socket = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            try {
                socket = serverSocket.accept();
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();

                SocketRequest request = new SocketRequest(inStream);
                request.parse();

                SocketResponse response = new SocketResponse(outStream, request);
                response.sendStaticFile();

                shutdown = request.getUri().endsWith(SHUTDOWN_CMD);

//                throw new IllegalArgumentException("message");
            }catch (Exception e) {
                e.printStackTrace();
                SocketResponse.sendError(outStream, e);
            }finally {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
