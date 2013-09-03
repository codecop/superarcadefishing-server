package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.Controller;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FishingMetricsTest {

    private FishingDataDao dao;
    private Controller metrics;

    @Test
    public void shouldPersistMetricsData() {
        final boolean[] persistWasCalled = new boolean[1];
        dao = new FishingDataDao(){
            public void persist(FishingData action) {
                assertNotNull(action.getInsertionTime());

                persistWasCalled[0] = true;
            }
        };

        metrics = new FishingMetrics(dao);

        metrics.process(new HashMap<String, Object>());

        assertTrue("did not call persist", persistWasCalled[0]);
    }

    // convert map to bean
    // verify mandatory values
    // add generated values - server timestanmp, metadata of metrics business
    // call persister

}
