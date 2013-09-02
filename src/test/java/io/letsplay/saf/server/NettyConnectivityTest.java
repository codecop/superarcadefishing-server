package io.letsplay.saf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertEquals;

public class NettyConnectivityTest {


    // param(1) just example to show the thread model with 2 connected clients while live coding
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    // param(1) just example to show the thread model with 2 connected clients while live coding
    EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    Channel channel;

    private WebDriver webDriver;
    private int wasClicked;

    @Before
    public void startNettyServer() throws InterruptedException {

        // additional thread pool for blocking handler
        final EventExecutorGroup executorGroup = new DefaultEventExecutorGroup(8);

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpRequestDecoder(),
                                new HttpObjectAggregator(65536),
                                new HttpResponseEncoder(),
                                new WebSocketServerProtocolHandler("/nettyConnectivity"));
//                                    new JSUGWebSocketHandler(channels)); // normal example without another thread pool

                        // register blocking or long lasting handler to additional thread pool
                        ch.pipeline().addLast(executorGroup, new ChannelInboundMessageHandlerAdapter<TextWebSocketFrame>() {

                            @Override
                            public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
                                wasClicked = wasClicked + 1;
                            }
                        });
                    }
                });

        channel = bootstrap.bind(8088).sync().channel();
    }


    @Before
    public void openFireFox() {
        webDriver = new FirefoxDriver();
    }

    @Test
    public void JsShouldCallNetty() throws InterruptedException {
        webDriver.get("file:///home/raphael/projects/superarcadefishing-server/src/test/resources/nettyConnectivity.html");
        Thread.sleep(2000);
        // channel.closeFuture().sync();
        // TODO check netty
        webDriver.findElement(By.id("btn")).click();
        assertEquals(1, wasClicked);
    }

    @After
    public void closeNetty() {
        bossGroup.shutdown();
        workerGroup.shutdown();
    }

    @After
    public void closeFireFox() {
        webDriver.close();
    }
}