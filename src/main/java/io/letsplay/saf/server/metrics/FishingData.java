package io.letsplay.saf.server.metrics;

import java.util.Date;

public class FishingData {

    private Fisherman fisherman;
    private GameProgress gameProgress;
    private RodThrown rodThrown;

    private Date insertionTime;

    public Date getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Date date) {
        this.insertionTime = date;
    }
}
