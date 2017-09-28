package v2.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by wangqi on 2017/9/26 下午7:17.
 */
public class ServletServer {
    private static final String SHUTDOWN_CMD = "/SHUTDOWN";
    private static boolean shutdown = false;

    public static void main(String[] args) {
        ServletServer ss = new ServletServer();
        ss.await();
    }

    public void await() {
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

        while (!shutdown) {
            Socket socket = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            try {
                socket = serverSocket.accept();
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();

                ServletRequestImpl request = new ServletRequestImpl(inStream);
                request.parse();

                ServletResponseImpl response = new ServletResponseImpl(outStream, request);
                if( request.getUri().contains("servlet") ) {
                    // !!!! 对于Servlet特殊处理
                    servletProcessor.process(request, response);
                }else {
                    response.sendStaticFile();
                }
                shutdown = request.getUri().endsWith(SHUTDOWN_CMD);

            }catch (Exception e) {
                e.printStackTrace();
                ServletResponseImpl.sendError(outStream, e);
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
