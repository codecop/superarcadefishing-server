package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.AttributesClassSetter;
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
        metricsData.insertionTime = new Date();

        new AttributesClassSetter(metricsData).set(attributes);

        // verify mandatory values

        // add generated values - e.g. day of week, week, metadata of metrics business

        dao.persist(metricsData);
    }
}
