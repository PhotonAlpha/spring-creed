package com.ethan.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @className: NettyServerHandler
 * @author: Ethan
 * @date: 4/3/2022
 * <p>
 * 1. 我们自定义一个handler,需要继承netty的适配器
 * 2. 这时自定的handler才能处理
 **/
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    //实际读取数据
    // 1. ChannelHandlerContext 上下文对象,含有pipeline,channel,address
    // 2. msg 为客户端数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx=" + ctx);
        //将msg转成byteBuf
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送消息是:" + byteBuf.toString(StandardCharsets.UTF_8));
        System.out.println("客户端地址是:" + ctx.channel().remoteAddress());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将数据写入到缓冲,并刷新
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端~", StandardCharsets.UTF_8));
    }

    //处理异常,需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
