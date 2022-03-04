package com.ethan.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @className: Client
 * @author: Ethan
 * @date: 4/3/2022
 **/
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        //客户端只需要一个事件循环
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            //创建一个启动对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)//设置线程组
                    .channel(NioSocketChannel.class)//设置客户端通道
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler());//加入自己的处理器
                        }
                    });
            System.out.println("客户端 is ready");
            //启动客户端连接服务端,设计netty的异步模型
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();

            //关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }


    }
}
