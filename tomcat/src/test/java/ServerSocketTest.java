import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketTest {
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader reader;
    private PrintWriter writer;

    public static void main(String[] args) {
        ServerSocketTest server = new ServerSocketTest();
        server.startServer();
    }
    public  void startServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(8080, 10, InetAddress.getByName("127.0.0.1"));//开启80端口
            System.out.println("服务器启动>>> 8080端口");
            while(true){//开启一个永远的循环，一直等待客户访问80端口
                Socket socket = serverSocket.accept();
//                es.submit(new Handler(socket));
//                System.out.println("收到新请求");
                inputStream = socket.getInputStream();//服务器到客户的输出流
                outputStream = socket.getOutputStream();//客户到服务器的输入流
                reader = new BufferedReader(new InputStreamReader(inputStream));//包装后的输入缓冲字符流
                writer = new PrintWriter(new OutputStreamWriter(outputStream));//包装后的输出缓冲字符流
                String msg;//接收客户端请求的临时字符串
                StringBuffer request = new StringBuffer();//将请求拼接成完整的请求
                while((msg = reader.readLine()) != null && msg.length() > 0){
                    request.append(msg);
                    request.append("\n");//HTTP协议的格式
                }
                String[] msgs = request.toString().split(" ");//HTTP协议以空格为分隔符
                //msgs[1]代表了HTTP协议中的第二个字符串，是浏览器请求的文件名

                String info = "HTTP/1.1 200 OK \r\n" +
                        "Content-type: text/html \r\n" +
                        "Content-Length: 23\r\n" +
                        "\r\n" +
                        "<h1> Hello World JJJJJJLAKS ya </h1>";
                writer.println(info);//如果服务器不打算对客户端发送.ico结尾的文件，可以这一写，然后直接返回，跳过发送该文件的过程
                writer.close();
                //如果不发送，就直接返回

                outputStream.close();
                inputStream.close();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
