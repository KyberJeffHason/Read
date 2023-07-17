package lotr.common.fellowshipQuests.storage;

import lotr.common.fellowship.LOTRFellowship;
import lotr.common.fellowship.LOTRFellowshipClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class GuildContainer extends Container {
    private final GuildInventory guildInventory;
    private final int guildInventoryRows = 4; // assuming you want a 8x4 grid for the guild inventory

    public GuildContainer(EntityPlayer player, LOTRFellowship fellowship) {
        this.guildInventory = fellowship.getInventory();
        slotsLogic(player);
    }

    public GuildContainer(EntityPlayer player, LOTRFellowshipClient fellowshipClient) {
        this.guildInventory = fellowshipClient.inventory;

        slotsLogic(player);
    }

    public void slotsLogic(EntityPlayer player) {

        InventoryPlayer playerInventory = player.inventory;

        for (int row = 0; row < guildInventoryRows; row++) {
            for (int col = 0; col < 8; col++) {
                this.addSlotToContainer(new Slot(guildInventory, col + row * 8, 8 + col * 18, 18 + row * 18));
            }
        }

        // add the player's inventory slots to the container
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // add the player's hotbar slots to the container
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        // return true if the player is an officer or owner of the guild
        // replace the condition with your own
        return true; // replace "PlayerName" with your check
    }
}