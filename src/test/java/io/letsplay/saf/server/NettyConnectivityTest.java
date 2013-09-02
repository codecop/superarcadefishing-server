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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertEquals;

public class NettyConnectivityTest {

    public static final String TEST_PAGE = "file:///home/raphael/projects/superarcadefishing-server/src/test/resources/nettyConnectivity.html";
    public static final String NETTY_URL = "/nettyConnectivity";
    public static final int NETTY_PORT = 8088;
    public static final String BUTTON_ID = "btn";

    public static final int WAIT_TIME = 500;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private int receivedCount;

    private class CountingInputHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
            receivedCount++;
        }
    }

    private WebDriver webDriver;

    @Before
    public void startNettyServer() throws InterruptedException {
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
                                new WebSocketServerProtocolHandler(NETTY_URL),
                                new CountingInputHandler());
                    }
                });

        bootstrap.bind(NETTY_PORT).sync();
    }

    @Before
    public void openFireFox() {
        webDriver = new FirefoxDriver();
    }

    @Test
    public void JsShouldCallNetty() throws InterruptedException {
        webDriver.get(TEST_PAGE);
        Thread.sleep(WAIT_TIME);
        webDriver.findElement(By.id(BUTTON_ID)).click();
        assertEquals(1, receivedCount);
    }

    @After
    public void shutdownNettyServer() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @After
    public void closeFireFox() {
        webDriver.close();
    }

}