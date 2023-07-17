package lotr.common.fellowshipQuests.questData;

public class QuestReq {


    private int level;
    private float chance;

    public QuestReq(int level, float chance) {
        this.chance = chance;
        this.level = level;
    }

    public float getChance() {
        return chance;
    }

    public int getLevel() {
        return level;
    }
}
