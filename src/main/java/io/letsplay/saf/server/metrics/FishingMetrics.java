package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.Controller;

import java.util.Date;
import java.util.Map;

public class FishingMetrics implements Controller {
    private final FishingDataDao dao;

    public FishingMetrics(FishingDataDao dao) {
        this.dao = dao;
    }

    @Override
    public void process(Map<String, Object> attributes) {
        FishingData metricsData = new FishingData();

        metricsData.setInsertionTime(new Date());

        dao.persist(metricsData);
    }
}
