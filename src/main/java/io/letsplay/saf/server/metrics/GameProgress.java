package io.letsplay.saf.server.metrics;

import java.util.Date;
import java.util.List;

/**
 * State of the game at a certain point in time.
 */
public class GameProgress {

    private Date sessionStarted;
    private Date gameStarted;
    private Date levelStarted;
    private Date waveStarted;

    private int numberOfGames;
    private String level;
    // private String environment; // e.g. oily, foggy, high gravitation
    private int wave;

    private List<Npc> npcs;

}
