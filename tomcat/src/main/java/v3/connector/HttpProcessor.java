package v3.connector;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wangqi on 2017/10/9.
 */
public class HttpProcessor {

    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();

        }catch (Exception e) {
            e.printStackTrace();
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
