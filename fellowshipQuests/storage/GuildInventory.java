package lotr.common.fellowshipQuests.storage;

import lotr.common.fellowship.LOTRFellowship;
import lotr.common.fellowship.LOTRFellowshipClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class GuildInventory implements IInventory {
    private ItemStack[] inventory = new ItemStack[32];

    private LOTRFellowship fs;

    private LOTRFellowshipClient fsClient;

    private boolean isServer;

    public GuildInventory(LOTRFellowship fs) {
        this.fs = fs;
        isServer = true;
    }

    public GuildInventory(LOTRFellowshipClient fsClient) {
        this.fsClient = fsClient;
        isServer = false;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack;

            if (this.inventory[slot].stackSize <= amount) {
                itemstack = this.inventory[slot];
                this.inventory[slot] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.inventory[slot].splitStack(amount);

                if (this.inventory[slot].stackSize == 0) {
                    this.inventory[slot] = null;
                }

                this.markDirty();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    /**
     * Adds an ItemStack to the inventory.
     *
     * @param stack the ItemStack to add
     * @return true if the item was added successfully, false otherwise
     */
    public boolean addItemStack(ItemStack stack) {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) == null) {
                this.setInventorySlotContents(i, stack);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a similar ItemStack from the inventory.
     *
     * @param stack the ItemStack to remove
     * @return true if the item was removed successfully, false otherwise
     */
    public boolean removeItemStack(ItemStack stack) {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack currentStack = this.getStackInSlot(i);
            if (currentStack != null && currentStack.isItemEqual(stack)) {
                this.setInventorySlotContents(i, null);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack = this.inventory[slot];
            this.inventory[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                this.getStackInSlot(i).writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        compound.setTag("Items", list);
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int slot = itemTag.getInteger("Slot");
            if (slot >= 0 && slot < this.getSizeInventory()) {
                this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
            }
        }
    }

    @Override
    public String getInventoryName() {
        return "Guild Inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if(isServer) {
            fs.onInventoryChanged(this);
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        // return true if the player is an officer or owner of the guild
        // replace the condition with your own
        return true; // replace "PlayerName" with your check
    }

    @Override
    public void openInventory() {
        // Any logic you need when the inventory is opened
    }

    @Override
    public void closeInventory() {
        // Any logic you need when the inventory is closed
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
}