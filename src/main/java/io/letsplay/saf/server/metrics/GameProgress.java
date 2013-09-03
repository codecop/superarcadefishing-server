package io.letsplay.saf.server.metrics;

import java.util.Date;
import java.util.List;

/**
 * State of the game at a certain point in time.
 */
class GameProgress {

    Date sessionStarted;
    Date gameStarted;
    Date levelStarted;
    Date waveStarted;

    int numberOfGames;
    String level;
    // private String environment; // e.g. oily, foggy, high gravitation
    int wave;

    List<Npc> npcs;
}
