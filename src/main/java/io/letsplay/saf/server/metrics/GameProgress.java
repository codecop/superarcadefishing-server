package io.letsplay.saf.server.metrics;

import io.letsplay.saf.server.ListType;

import java.util.Date;
import java.util.List;

/**
 * State of the game at a certain point in time.
 */
public class GameProgress {

    public Date sessionStarted;
    public Date gameStarted;
    public Date levelStarted;
    public Date waveStarted;

    public int numberOfGames;
    public String level;
    // public String environment; // e.g. oily, foggy, high gravitation
    public int wave;

    @ListType(Npc.class)
    public List<Npc> npcs;
}
