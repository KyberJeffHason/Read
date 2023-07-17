package lotr.common.fellowshipQuests;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.common.fellowship.LOTRFellowship;
import lotr.common.fellowship.LOTRFellowshipData;
import lotr.common.fellowship.ROMEFellowShip;
import lotr.common.libs.ROMELib;
import lotr.rome.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.UUID;

public abstract class QuestBase {

    protected String name;


    protected int completedObjective;
    protected int objectiveCount;

    private UUID fs;


    public QuestBase() {
        this.fs = fs;
        this.completedObjective = 0;
    }


    public QuestBase(UUID fs) {
        this.fs = fs;
        this.completedObjective = 0;
    }


    public int getObjectiveCount() {
        return objectiveCount;
    }

    public String getName() {
        return name;
    }

    public ROMEFellowShip getFellowship() {
        return LOTRFellowshipData.getFellowship(fs);
    }

    public int getCompletedObjective() {
        return completedObjective;
    }

    public void setCompletedObjective(int completedObjectivee) {
        this.completedObjective = completedObjectivee;
    }

    public void doUpdate (LivingEvent.LivingUpdateEvent event) {

    }

    public void doDeath (LivingDeathEvent event) {


    }


    public void onCompletion (LOTRFellowship fs) {

    }


}
