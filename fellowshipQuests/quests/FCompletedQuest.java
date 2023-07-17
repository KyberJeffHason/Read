package lotr.common.fellowshipQuests.quests;

import lotr.common.fellowship.ROMEFellowShip;
import lotr.common.fellowshipQuests.QuestBase;

import java.util.UUID;

public class FCompletedQuest extends QuestBase {

    public FCompletedQuest() {
        super();
    }

    public FCompletedQuest(UUID fs) {
        super(fs);
        this.name = "Sex";
        this.objectiveCount = 0;
    }
}
