package com.ethan.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @className: Server
 * @author: Ethan
 * @date: 4/3/2022
 **/
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //创建bossGroup和ClientGroup
        //1.创建两个线程组
        //2.bossGroup只是处理连接请求,真正的和客户端业务处理,交给workerGroup
        //3.两个都是无限循环,默认电脑核数 * 2
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        //创建服务端的启动对象,配置参数
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)//设置两个参数
                    .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel作为服务器的通道
                    .option(ChannelOption.SO_BACKLOG, 128)//设置线程队列等待的连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建通道测试对象
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            System.out.println("服务器 is ready");
            //绑定一个端口,并且同步,生成ChannelFuture对象
            ChannelFuture cf = bootstrap.bind(6668).sync();
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
