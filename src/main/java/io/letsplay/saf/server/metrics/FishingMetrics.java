package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.Controller;
import io.letsplay.saf.server.ReflectionSetter;

import java.util.Date;
import java.util.Map;

public class FishingMetrics implements Controller {
    public static final String FISHER_MAN = "fisherman";
    public static final String GAME_PROGRESS = "gameprogress";
    public static final String ROD_THROWN = "rodthrown";
    private final FishingDataDao dao;
    private final ReflectionSetter reflectionSetter;

    public FishingMetrics(FishingDataDao dao, ReflectionSetter reflectionSetter) {
        this.dao = dao;
        this.reflectionSetter = reflectionSetter;
    }

    @Override
    public void process(Map<String, Object> attributes) {
        FishingData metricsData = new FishingData();

        FisherMan fisherMan = new FisherMan();
        @SuppressWarnings("unchecked")
        final Map<String, Object> fisherManValues = (Map<String, Object>) attributes.get(FISHER_MAN);

        reflectionSetter.set(fisherMan, fisherManValues);
        metricsData.fisherMan = fisherMan;

        GameProgress gameProgress = new GameProgress();
        @SuppressWarnings("unchecked")
        final Map<String, Object> gameProgressValues = (Map<String, Object>) attributes.get(GAME_PROGRESS);

        reflectionSetter.set(gameProgress, gameProgressValues);
        metricsData.gameProgress = gameProgress;

        RodThrown rodThrown = new RodThrown();
        @SuppressWarnings("unchecked")
        final Map<String, Object> rodThrownValues = (Map<String, Object>) attributes.get(ROD_THROWN);

        reflectionSetter.set(rodThrown, rodThrownValues);
        metricsData.rodThrown = rodThrown;

        metricsData.insertionTime = new Date();

        dao.persist(metricsData);
    }
}
