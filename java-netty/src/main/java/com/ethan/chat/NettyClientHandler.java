package com.ethan.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @className: NettyClientHandler
 * @author: Ethan
 * @date: 4/3/2022
 **/
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    //通道就绪就触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client =" + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello server", StandardCharsets.UTF_8));

    }

    //当通道有读取事件时会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务器回复:" + byteBuf.toString(StandardCharsets.UTF_8));
        System.out.println("服务器地址是:" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
