package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.Controller;
import io.letsplay.saf.server.AttributesDtoSetter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FishingMetrics implements Controller {
    public static final String FISHER_MAN = "fisherMan";
    public static final String GAME_PROGRESS = "gameProgress";
    public static final String ROD_THROWN = "rodThrown";
    public static final String NPCS = "npcs";

    private final AttributesDtoSetter attributesDtoSetter;
    private final FishingDataDao dao;

    public FishingMetrics(AttributesDtoSetter attributesDtoSetter, FishingDataDao dao) {
        this.attributesDtoSetter = attributesDtoSetter;
        this.dao = dao;
    }

    @Override
    public void process(Map<String, Object> attributes) {
        FishingData metricsData = new FishingData();

        FisherMan fisherMan = new FisherMan();
        @SuppressWarnings("unchecked")
        final Map<String, Object> fisherManValues = (Map<String, Object>) attributes.get(FISHER_MAN);

        attributesDtoSetter.set(fisherMan, fisherManValues);
        metricsData.fisherMan = fisherMan;

        GameProgress gameProgress = new GameProgress();
        @SuppressWarnings("unchecked")
        final Map<String, Object> gameProgressValues = (Map<String, Object>) attributes.get(GAME_PROGRESS);

        attributesDtoSetter.set(gameProgress, gameProgressValues);

        if (attributes.containsKey(NPCS)) {
            List<Npc> npcs = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> npcsValues = (List<Map<String, Object>>) attributes.get(NPCS);
            for (Map<String, Object> npcValues : npcsValues) {

                Npc npc = new Npc();
                attributesDtoSetter.set(npc, npcValues);
                npcs.add(npc);
            }
            gameProgress.npcs = npcs;
        }

        metricsData.gameProgress = gameProgress;

        RodThrown rodThrown = new RodThrown();
        @SuppressWarnings("unchecked")
        final Map<String, Object> rodThrownValues = (Map<String, Object>) attributes.get(ROD_THROWN);

        attributesDtoSetter.set(rodThrown, rodThrownValues);
        metricsData.rodThrown = rodThrown;

        metricsData.insertionTime = new Date();

        dao.persist(metricsData);
    }
}
