package lotr.common.fellowshipQuests.level;

import lotr.common.fellowshipQuests.QuestBase;
import lotr.common.fellowshipQuests.level.lvls.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LevelRegister {

    public static final List<LevelBase> Flevels = new ArrayList<>();


    public static void registerLevels() {
        Flevels.add(new FLevel1());
        Flevels.add(new FLevel2());
        Flevels.add(new FLevel3());
        Flevels.add(new FLevel4());
        Flevels.add(new FLevel5());
        Flevels.add(new FLevel6());
        Flevels.add(new FLevel7());
        Flevels.add(new FLevel8());
        Flevels.add(new FLevel9());
        Flevels.add(new FLevel10());
    }

    public static LevelBase getLevelbyNum(int id) {
        return Flevels.get(id - 1);
    }


}
