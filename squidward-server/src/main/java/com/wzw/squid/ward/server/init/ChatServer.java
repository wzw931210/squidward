package com.wzw.squid.ward.server.init;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wuzhiwei
 * @date 2021/3/22
 */
@Slf4j
public class ChatServer {

    private final int port;

    public ChatServer(int port){
        this.port = port;
        start();
    }

    public void start(){
        // 配置服务端的nio线程组
        //实际上EventLoopGroup就是Reactor线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    //回调
                    .childHandler(new ChatServerInitializer())
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.TCP_NODELAY, true);
        //绑定监听端口，调用sync同步阻塞方法等待绑定操作完成，完成后返回ChannelFuture类似于JDK中Future
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //使用sync方法进行阻塞，等待服务端链路关闭之后Main函数才退出
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("[server] channel connect interrupted!",e);
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer(23334);
    }
}
