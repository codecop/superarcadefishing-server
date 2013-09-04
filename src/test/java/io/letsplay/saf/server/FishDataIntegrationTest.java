package io.letsplay.saf.server;

import io.letsplay.saf.server.connector.JsonMapper;
import io.letsplay.saf.server.connector.ParserHandler;
import io.letsplay.saf.server.connector.Server;
import io.letsplay.saf.server.metrics.FishingData;
import io.letsplay.saf.server.metrics.FishingDataDao;
import io.letsplay.saf.server.metrics.FishingMetrics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FishDataIntegrationTest {

    public static final String TEST_PAGE = "file:///home/raphael/projects/superarcadefishing-server/src/test/resources/fishDataIntegration.html";
    public static final String NETTY_URL = "/fishDataIntegration";
    public static final int NETTY_PORT = 8089;
    public static final String BUTTON_ID = "btn";

    public static final int WAIT_TIME = 500;

    private Server server = new Server(NETTY_URL, NETTY_PORT, createApplication());

    private ParserHandler createApplication() {
        final FishingDataDao dao = new FishingDataDao() {
            @Override
            public void persist(FishingData data) {
                System.out.println("got " + data.insertionTime);
                // TODO assert here
            }
        };

        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.register("metrics_fishing_event", new FishingMetrics(dao));
        // later add more controllers here

        return new ParserHandler(dispatcher, new JsonMapper());
    }

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
    public void fullJsonRequestShouldEndInDao() throws InterruptedException {
        webDriver.get(TEST_PAGE);
        Thread.sleep(WAIT_TIME);
        webDriver.findElement(By.id(BUTTON_ID)).click();
    }

    @After
    public void shutdownNettyServer() {
        server.shutDown();
    }

    @After
    public void closeFireFox() throws InterruptedException {
        webDriver.close();
    }

}