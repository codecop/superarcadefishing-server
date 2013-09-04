package io.letsplay.saf.server.connector;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class Server {

    private final String nettyUrl;
    private final int nettyPort;
    private final ChannelInboundHandler inboundHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Server(String nettyUrl, int nettyPort, ChannelInboundHandler inboundHandler) {
        this.nettyUrl = nettyUrl;
        this.nettyPort = nettyPort;
        this.inboundHandler = inboundHandler;
    }

    public void run() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpRequestDecoder(),
                                new HttpObjectAggregator(65536),
                                new HttpResponseEncoder(),
                                new WebSocketServerProtocolHandler(nettyUrl),
                                inboundHandler);
                    }
                });

        bootstrap.bind(nettyPort).sync();
    }

    public void shutDown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
