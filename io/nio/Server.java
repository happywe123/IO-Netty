package ReadAgain.ioandNetty.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO: 单线程模式，Selector 通过轮询机制，用一个线程去监听客户端连接事件，
 * 连接之后，通道建立完成，Selector 还要自己监听连接的通道上发生的读写事件
 */

public class Server {
    public static void main(String[] args) throws IOException {
        //对 ServerSocket 进行封装，可以同时读和写，不用像 ServerSocket 需要拿到 inputStresm 和 outputStream
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8888));
        // 设定阻塞为 false,所以为非阻塞模型
        ssc.configureBlocking(false);

        System.out.println("server started, listening on :" + ssc.getLocalAddress());
        // 打开 selection,并注册对那件事感兴趣，这里是对连接感兴趣
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        //轮询，等待的过程是阻塞的
        while(true) {
            selector.select();
            //selector 在每个端口上注册了监听器，一个监听器对应一个 key
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while(it.hasNext()) {
                //找到发生事件的 key,从集合中移除，并进行处理
                SelectionKey key = it.next();
                it.remove();
                handle(key);
            }
        }
    }

    //处理函数
    private static void handle(SelectionKey key) {
        //如果是连接，说明有客户端想要连接
        if(key.isAcceptable()) {
            try {
                //建立连接通道，通道连接之后，再在通道上注册监听事件，监听读写操作
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(key.selector(), SelectionKey.OP_READ );
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }

        //读写操作
        else if (key.isReadable()) { //flip
            SocketChannel sc = null;
            try {
                sc = (SocketChannel)key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(512);
                buffer.clear();
                int len = sc.read(buffer);

                if(len != -1) {
                    System.out.println(new String(buffer.array(), 0, len));
                }

                ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
                sc.write(bufferToWrite);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(sc != null) {
                    try {
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
