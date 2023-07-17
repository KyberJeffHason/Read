package lotr.common.fellowshipQuests.quests;

import lotr.common.fellowship.LOTRFellowship;
import lotr.common.fellowship.ROMEFellowShip;
import lotr.common.fellowshipQuests.QuestBase;
import lotr.common.fellowshipQuests.QuestRegister;
import lotr.common.itemreg.ROMEItems;
import lotr.common.libs.ROMELib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.UUID;

public class FQuestKillMobs extends QuestBase {

    public FQuestKillMobs() {

    }

    public FQuestKillMobs(UUID fs) {
        super(fs);
        this.name = "Убить кротов нахуй";
        this.objectiveCount = 10;
    }


    @Override
    public void doDeath(LivingDeathEvent event) {
        if(!event.entity.worldObj.isRemote) {
            this.setCompletedObjective(getCompletedObjective() + 1);
            if(getFellowship() instanceof LOTRFellowship) {
               System.out.println(((LOTRFellowship) getFellowship()).fellowshipName);
            }
            getFellowship().markDirtyData();
            System.out.println(getCompletedObjective());
        }
    }


    @Override
    public void onCompletion (LOTRFellowship fs) {
        fs.SetXP(fs.getXp() + 100);
        fs.getInventory().addItemStack(new ItemStack(ROMEItems.magnitit));
    }


}
