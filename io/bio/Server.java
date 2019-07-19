package ReadAgain.ioandNetty.io.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        //启动服务器的服务
        ServerSocket ss = new ServerSocket();

        //提供 IP 地址和监听端口
        ss.bind(new InetSocketAddress("127.0.0.1", 8888));

        //通过套接字进行通信
        while(true) {
            Socket s = ss.accept(); //阻塞方法

            new Thread(() -> {
                handle(s);
            }).start();
        }
    }

    //处理从客户端接受到的数据
    static void handle(Socket s) {
        try {
            byte[] bytes = new byte[1024];
            int len = s.getInputStream().read(bytes); // 阻塞方法
            System.out.println(new String(bytes, 0, len));

            s.getOutputStream().write(bytes, 0, len);  // 阻塞方法
            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
