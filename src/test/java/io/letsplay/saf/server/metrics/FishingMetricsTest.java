package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.Controller;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FishingMetricsTest {

    private FishingDataDao dao;
    private Controller metrics;

    @Test
    public void shouldPersistMetricsData() {
        final boolean[] persistWasCalled = new boolean[1];
        dao = new FishingDataDao() {
            public void persist(FishingData action) {
                assertNotNull(action.insertionTime);
                persistWasCalled[0] = true;
            }
        };

        metrics = new FishingMetrics(dao);
        metrics.process(new HashMap<String, Object>());

        assertTrue("did not call persist", persistWasCalled[0]);
    }

    @Test
    public void shouldConvertAttributesToDto() {
        dao = new FishingDataDao() {
            public void persist(FishingData action) {
                assertEquals(1, action.fisherMan.score);
                assertEquals("1st", action.gameProgress.level);
                assertEquals(0, action.rodThrown.xCoordinate);
                assertEquals("Hugo", action.gameProgress.npcs.get(0).name);
            }
        };

        metrics = new FishingMetrics(dao);

        Map<String, Object> fisherMan = new HashMap<>();
        fisherMan.put("score", 1);
        Map<String, Object> gameProgress = new HashMap<>();
        gameProgress.put("level", "1st");
        Map<String, Object> rodThrown = new HashMap<>();
        rodThrown.put("xCoordinate", 0);

        Map<String, Object> npc = new HashMap<>();
        npc.put("name", "Hugo");
        gameProgress.put("npcs", Arrays.asList(npc));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("fisherMan", fisherMan);
        attributes.put("gameProgress", gameProgress);
        attributes.put("rodThrown", rodThrown);

        metrics.process(attributes);
    }

    // verify mandatory values
    // add generated values - server timestamp, metadata of metrics business

}
