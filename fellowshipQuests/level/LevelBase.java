package lotr.common.fellowshipQuests.level;

import lotr.common.fellowship.LOTRFellowship;

public abstract class LevelBase {

    protected int xpToReach;


    public LevelBase() {

    }

    public void onReached(LOTRFellowship fs) {

    }

    public int getXpToReach() {
        return xpToReach;
    }

    public abstract String getDescription();

}
