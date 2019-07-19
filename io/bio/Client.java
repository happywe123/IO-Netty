package ReadAgain.ioandNetty.io.bio;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        //使用套接字进行通信
        Socket s = new Socket("127.0.0.1", 8888);

        //向服务端发送数据
        s.getOutputStream().write("HelloServer".getBytes());
        s.getOutputStream().flush();
        //s.getOutputStream().close();
        System.out.println("write over, waiting for msg back...");

        //接受服务端发来的数据
        byte[] bytes = new byte[1024];
        int len = s.getInputStream().read(bytes);
        System.out.println(new String(bytes, 0, len));
        s.close();


    }
}
