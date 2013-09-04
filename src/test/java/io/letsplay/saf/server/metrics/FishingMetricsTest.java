package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.Controller;
import io.letsplay.saf.server.AttributesDtoSetter;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FishingMetricsTest {

    private AttributesDtoSetter setter;
    private FishingDataDao dao;
    private Controller metrics;

    @Test
    public void shouldPersistMetricsData() {
        setter = mock(AttributesDtoSetter.class);

        final boolean[] persistWasCalled = new boolean[1];
        dao = new FishingDataDao() {
            public void persist(FishingData action) {
                assertNotNull(action.insertionTime);
                persistWasCalled[0] = true;
            }
        };

        metrics = new FishingMetrics(setter, dao);
        metrics.process(new HashMap<String, Object>());

        assertTrue("did not call persist", persistWasCalled[0]);
    }

    @Test
    public void shouldConvertAttributesToDto() {
        setter = new AttributesDtoSetter() {
            @Override
            public void set(Object dto, Map<String, Object> attributes) {
                if (dto instanceof FisherMan)
                    ((FisherMan) dto).score = 1;
                else if (dto instanceof GameProgress)
                    ((GameProgress) dto).level = "1st";
                else if (dto instanceof RodThrown)
                    ((RodThrown) dto).xCoordinate = 0;
                else if (dto instanceof Npc)
                    ((Npc) dto).name = "npc";
            }
        };

        dao = new FishingDataDao() {
            public void persist(FishingData action) {
                assertEquals(1, action.fisherMan.score);
                assertEquals("1st", action.gameProgress.level);
                assertEquals(0, action.rodThrown.xCoordinate);
                assertEquals("npc", action.gameProgress.npcs.get(0).name);
            }
        };

        metrics = new FishingMetrics(setter, dao);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("npcs", Arrays.asList(new HashMap<String, Object>()));

        metrics.process(attributes);
    }

    // convert map to bean
    // verify mandatory values
    // add generated values - server timestamp, metadata of metrics business

}
