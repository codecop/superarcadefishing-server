package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.Controller;
import io.letsplay.saf.server.ReflectionSetter;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class FishingMetricsTest {

    private FishingDataDao dao;
    private Controller metrics;

    @Test
    public void shouldCallReflectionSetterAndPersistMetricsData() {
        ReflectionSetter setterMock = mock(ReflectionSetter.class);

        final boolean[] persistWasCalled = new boolean[1];
        dao = new FishingDataDao(){
            public void persist(FishingData action) {
                assertNotNull(action.insertionTime);

                persistWasCalled[0] = true;
            }
        };

        metrics = new FishingMetrics(dao, setterMock);

        metrics.process(new HashMap<String, Object>());

        assertTrue("did not call persist", persistWasCalled[0]);

        verify(setterMock, times(3)).set(anyObject(), anyMapOf(String.class, Object.class));
    }

    @Test
    public void shouldConvertMapToBeanAndPersistMetricsData() {
        ReflectionSetter reflectionMock = new ReflectionSetter() {
            @Override
            public void set(Object bean, Map<String, Object> valuesToSetFields) {
                if (bean instanceof FisherMan)
                    ((FisherMan) bean).score = 1;
                else if (bean instanceof GameProgress)
                    ((GameProgress) bean).level = "1st";
                else if (bean instanceof RodThrown)
                    ((RodThrown) bean).xCoordinate = 0;
                else if (bean instanceof Npc)
                    ((Npc) bean).name = "npc";
            }
        };

        final boolean[] persistWasCalled = new boolean[1];
        dao = new FishingDataDao(){
            public void persist(FishingData action) {
                assertNotNull(action.insertionTime);

                assertEquals(1, action.fisherMan.score);
                assertEquals("1st", action.gameProgress.level);
                assertEquals(0, action.rodThrown.xCoordinate);
                assertEquals("npc", action.gameProgress.npcs.get(0).name);

                persistWasCalled[0] = true;
            }
        };

        metrics = new FishingMetrics(dao, reflectionMock);

        @SuppressWarnings("unchecked")
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("npcs", Arrays.asList(new HashMap<String, Object>()));

        metrics.process(attributes);

        assertTrue("did not call persist", persistWasCalled[0]);
    }

    // convert map to bean
    // verify mandatory values
    // add generated values - server timestamp, metadata of metrics business
    // call persist

}
