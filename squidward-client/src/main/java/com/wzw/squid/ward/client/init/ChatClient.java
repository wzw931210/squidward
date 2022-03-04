package com.wzw.squid.ward.client.init;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author wuzhiwei
 * @date 2021/3/22
 */
@Slf4j
public class ChatClient {

    private final String host;

    private final int port;

    public ChatClient(String host, int port){
        this.host = host;
        this.port = port;
        start();
    }


    public void start()  {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .remoteAddress(host,port)
                    .handler(new ChatClientInitializer())
                    .option(ChannelOption.TCP_NODELAY, true);
            Channel channel = bootstrap.connect(host, port).sync().channel();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true){
                try {
                    channel.writeAndFlush(reader.readLine() + "\n");
                }catch (IOException e){
                    log.error("把标准输入打到控制台，异常",e);
                    break;
                }
            }
        }catch (InterruptedException e){
            log.error("[client] channel connect interrupted!",e);
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatClient("127.0.0.1",23334);
    }
}


