package io.letsplay.saf.server;

import io.letsplay.saf.server.connector.JsonMapper;
import io.letsplay.saf.server.connector.ParserHandler;
import io.letsplay.saf.server.connector.Server;
import io.letsplay.saf.server.metrics.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.*;

public class FishDataIntegrationTest {

    public static final String TEST_PAGE = "fishDataIntegration.html";
    public static final String NETTY_URL = "/fishDataIntegration";
    public static final int NETTY_PORT = 8089;
    public static final String BUTTON_ID = "btn";

    public static final int WAIT_TIME = 500;

    private Server server = new Server(NETTY_URL, NETTY_PORT, createApplication());
    private WebDriver webDriver;

    private boolean daoWasCalled;
    private AssertionError assertionError;

    private ParserHandler createApplication() {
        final FishingDataDao dao = new FishingDataDao() {
            @Override
            public void persist(FishingData data) {
                asynchronousAssertJsonValues(data);
            }
        };

        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.register("metrics_fishing_event", new FishingMetrics(dao));
        // later add more controllers here

        return new ParserHandler(dispatcher, new JsonMapper());
    }

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
        final URL testPage = getClass().getClassLoader().getResource(TEST_PAGE);
        assertNotNull(testPage);

        webDriver.get(testPage.toString());
        Thread.sleep(WAIT_TIME);
        webDriver.findElement(By.id(BUTTON_ID)).click();

        failOnAsynchronousError();
    }

    private synchronized void asynchronousAssertJsonValues(FishingData data) {
        daoWasCalled = true;
        try {
            assertFishingData(data);
        } catch (AssertionError ex) {
            assertionError = ex;
        }
    }

    private void assertFishingData(FishingData data) {
        assertNotNull(data.insertionTime);

        final FisherMan fisherMan = data.fisherMan;
        assertNotNull(fisherMan);
        assertEquals(3500, fisherMan.score);
        assertEquals(20, fisherMan.scoreMultiplier);
        assertEquals(20, fisherMan.remainingTime);
        assertEquals(Arrays.asList("dynamite", "harpoon"), fisherMan.upgrades);

        assertNotNull(data.gameProgress);
        assertNotNull(data.gameProgress.sessionStarted);
        assertNotNull(data.gameProgress.gameStarted);
        assertNotNull(data.gameProgress.levelStarted);
        assertNotNull(data.gameProgress.waveStarted);
        assertEquals(2, data.gameProgress.numberOfGames);
        assertEquals("Nordkap", data.gameProgress.level);
        assertEquals(3, data.gameProgress.wave);
        assertEquals(2, data.gameProgress.npcs.size());
        assertNpc(2, 5, "goldfish", 1, 0, data.gameProgress.npcs.get(0));
        assertNpc(7, 10, "shark", 5, -1, data.gameProgress.npcs.get(1));

        assertNotNull(data.rodThrown);
        assertNotNull(data.rodThrown.time);
        assertEquals("normalRod", data.rodThrown.which);
        assertEquals(5, data.rodThrown.xCoordinate);
        assertEquals("catch", data.rodThrown.result);
        assertNpc(5, 15, "goldfish", -1, 3, data.rodThrown.caught);
    }

    private void assertNpc(int xCoordinate, int yCoordinate, String name, int xDirectionVector, int yDirectionVector, Npc npc) {
        assertEquals(xCoordinate, npc.xCoordinate);
        assertEquals(yCoordinate, npc.yCoordinate);
        assertEquals(name, npc.name);
        assertEquals(xDirectionVector, npc.xDirectionVector);
        assertEquals(yDirectionVector, npc.yDirectionVector);
    }

    private synchronized void failOnAsynchronousError() {
        assertTrue("dao was not called", daoWasCalled);
        if (assertionError != null) {
            throw assertionError;
        }
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