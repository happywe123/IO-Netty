package ReadAgain.ioandNetty.netty;

import ReadAgain.ioandNetty.io.aio.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;


/**
 * Netty: 实现对于 NIO，BIO 的封装，封装成 AIO 的样子，
 * 在 Linux 下 AIO 的效率未必比 NIO 高。
 * 为什么？因为 NIO，AIO 的底层实现一样，AIO 多了一层封装
 *
 */


public class HelloNetty {
    public static void main(String[] args) {
        new NettyServer(8888).serverStart();
    }
}

class NettyServer {


    int port = 8888;

    public NettyServer(int port) {
        this.port = port;
    }

    public void serverStart() {
        // 两个线程池，相当于 NIO 里的 boss 和 workers
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 对 server 进行启动前的配置
        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // 通道连接
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    //连接后的监听
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Handler());
                    }
                });
        try {
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

class Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //super.channelRead(ctx, msg);
        System.out.println("server: channel read");
        ByteBuf buf = (ByteBuf)msg;
        System.out.println(buf.toString(CharsetUtil.UTF_8));
        ctx.writeAndFlush(msg);
        ctx.close();

        //buf.release();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
