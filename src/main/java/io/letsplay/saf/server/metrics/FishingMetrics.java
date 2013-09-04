package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.AttributesClassSetter;
import io.letsplay.saf.server.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FishingMetrics implements Controller {

    private final FishingDataDao dao;

    public FishingMetrics(FishingDataDao dao) {
        this.dao = dao;
    }

    @Override
    public void process(Map<String, Object> attributes) {
        FishingData metricsData = new FishingData();
        new AttributesClassSetter(metricsData).set(attributes);

        metricsData.insertionTime = new Date();

        dao.persist(metricsData);
    }
}
