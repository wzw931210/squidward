package com.wzw.squid.ward.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wuzhiwei
 * @date 2021/3/22
 */
@Slf4j
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /**
     * 当有客户端连接时，handlerAdded会执行,就把该客户端的通道记录下来，加入队列
     * @param var1 var1
     */
    @Override
    public void handlerAdded(ChannelHandlerContext var1) {
        // 获得客户端通道
        Channel incoming = var1.channel();
        if(!CHANNELS.contains(incoming)){
            incoming.writeAndFlush("[欢迎: " + incoming.remoteAddress() + "] 进入聊天室！\n");
        }
        // 加入队列
        CHANNELS.add(incoming);
    }

    /**
     * 断开连接
     * @param var1 var1
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext var1) {
        Channel leave = var1.channel();
        if(!CHANNELS.contains(leave)){
            leave.writeAndFlush("[再见: " + leave.remoteAddress() + "] 离开聊天室！\n");
        }
        CHANNELS.remove(leave);
    }

    /**
     * 当服务器监听到客户端活动时
     * @param var1 var1
     */
    @Override
    public void channelActive(ChannelHandlerContext var1) {
        Channel channel = var1.channel();
        System.out.println("[" + channel.remoteAddress() + "]: 在线");
    }

    /**
     * 离线
     * @param var1 var1
     */
    @Override
    public void channelInactive(ChannelHandlerContext var1) {
        Channel inComing = var1.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 离线");
    }

    /**
     * 每当从客户端有消息写入时
     * @param channelHandlerContext context
     * @param s s
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        Channel incoming = channelHandlerContext.channel();
        for (Channel channel : CHANNELS) {
            if(!channel.equals(incoming)) {
                channel.writeAndFlush("[用户" + incoming.remoteAddress() + " 说：]" + s + "\n");
            }else {
                channel.writeAndFlush("[我说：]" + s + "\n");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel inComing = ctx.channel();
        System.out.println(inComing.remoteAddress() + "通讯异常！");
        ctx.close();
    }

}
