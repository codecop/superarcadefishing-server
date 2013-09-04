package io.letsplay.saf.server.connector;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
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

    private int receivedCount;

    private class CountingInputHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
            receivedCount++;
        }
    }

    private Server server = new Server(NETTY_URL, NETTY_PORT, new CountingInputHandler());

    private WebDriver webDriver;

    @Before
    public void startNettyServer() throws InterruptedException {
        server.run();
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
        server.shutDown();
    }

    @After
    public void closeFireFox() {
        webDriver.close();
    }

}