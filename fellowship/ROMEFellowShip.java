package lotr.common.fellowship;

import lotr.common.fellowshipQuests.QuestBase;
import lotr.common.fellowshipQuests.QuestRegister;
import lotr.common.fellowshipQuests.quests.FCompletedQuest;

import java.util.ArrayList;
import java.util.UUID;


public class ROMEFellowShip {

    protected boolean needsSave = false;

    protected byte[] questData;

    protected UUID fellowshipUUID;

    protected ArrayList<QuestBase> quests = new ArrayList<>();


    public void markDirty() {
        needsSave = true;
    }

    public void markDirtyData() {
        questData = QuestRegister.serializeData(quests);
        System.out.println(quests.get(1).getCompletedObjective() + "sex");
        markDirty();
    }


    public void giveQuest(QuestBase q) {
        quests.add(q);
        markDirtyData();
    }

    public void setQuest(QuestBase q, int i) {
        quests.set(i,q);
        markDirtyData();
    }

    public UUID getFellowshipID() {
        return fellowshipUUID;
    }

    public void removeQuest(int i) {
        quests.remove(i);
        quests.add(i, new FCompletedQuest(this.getFellowshipID()));
        markDirtyData();
    }



}
