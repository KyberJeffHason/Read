package lotr.common.fellowship;

import java.util.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import lotr.common.fellowshipQuests.QuestBase;
import lotr.common.fellowshipQuests.level.LevelRegister;
import lotr.common.fellowshipQuests.quests.FCompletedQuest;
import lotr.common.fellowshipQuests.storage.GuildInventory;
import lotr.common.network.base.PacketDispatcher;
import lotr.common.network.serverToClient.PacketSyncGuildStorage;
import noppes.npcs.controllers.data.Quest;
import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import lotr.common.LOTRLevelData;
import lotr.common.network.*;
import lotr.common.util.LOTRLog;
import net.minecraft.entity.player.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraftforge.common.ForgeHooks;

import static lotr.common.fellowshipQuests.QuestRegister.getData;
import static lotr.common.fellowshipQuests.QuestRegister.serializeData;

public class LOTRFellowship extends ROMEFellowShip {
	public String fellowshipName;
	public boolean disbanded = false;
	public ItemStack fellowshipIcon;
	public UUID ownerUUID;
	public List<UUID> memberUUIDs = new ArrayList<>();
	public Set<UUID> adminUUIDs = new HashSet<>();
	public Set<UUID> waypointSharerUUIDs = new HashSet<>();
	public boolean doneRetroactiveWaypointSharerCheck = true;
	public boolean preventPVP = true;

	private GuildInventory inventory = new GuildInventory(this);

	public boolean preventHiredFF = true;
	public boolean showMapLocations = true;
	public boolean isUpgraded;

	public int level = 1;

	public String faction;

	public int xp = 0;

	public String lastQuestDate = "";

	public int color = 0xFFFFFF;


	public LOTRFellowship() {
		fellowshipUUID = UUID.randomUUID();
	}

	public LOTRFellowship(UUID fsID) {
		fellowshipUUID = fsID;
	}

	public LOTRFellowship(UUID owner, String name) {
		this();
		ownerUUID = owner;
		fellowshipName = name;
		quests.add(new FCompletedQuest(this.getFellowshipID()));
		quests.add(new FCompletedQuest(this.getFellowshipID()));
		quests.add(new FCompletedQuest(this.getFellowshipID()));
		questData = serializeData(quests);
	}

	public void addMember(UUID player) {
		if (!isOwner(player) && !memberUUIDs.contains(player)) {
			memberUUIDs.add(player);
			LOTRLevelData.getData(player).addFellowship(this);
			updateForAllMembers(new FellowshipUpdateType.AddMember(player));
			markDirty();
		}
	}

	public boolean containsPlayer(UUID player) {
		return isOwner(player) || hasMember(player);
	}

	public void createAndRegister() {
		LOTRFellowshipData.addFellowship(this);
		LOTRLevelData.getData(ownerUUID).addFellowship(this);
		updateForAllMembers(new FellowshipUpdateType.Full());
		markDirty();
	}

	public void doRetroactiveWaypointSharerCheckIfNeeded() {
		if (!doneRetroactiveWaypointSharerCheck) {
			waypointSharerUUIDs.clear();
			if (!disbanded) {
				List<UUID> allPlayersSafe = getAllPlayerUUIDs();
				for (UUID player : allPlayersSafe) {
					if (!LOTRLevelData.getData(player).hasAnyWaypointsSharedToFellowship(this)) {
						continue;
					}
					waypointSharerUUIDs.add(player);
				}
				LOTRLog.logger.info("Fellowship " + getName() + " did retroactive waypoint sharer check and found " + waypointSharerUUIDs.size() + " out of " + allPlayersSafe.size() + " players");
			}
			doneRetroactiveWaypointSharerCheck = true;
			markDirty();
		}
	}

	public List<UUID> getAllPlayerUUIDs() {
		ArrayList<UUID> list = new ArrayList<>();
		list.add(ownerUUID);
		list.addAll(memberUUIDs);
		return list;
	}

	public UUID getFellowshipID() {
		return fellowshipUUID;
	}

	public boolean isUpgraded() {return isUpgraded;}

	public String getFaction() { return faction; }

	public int getLevel() {return level; }

	public int getColor() { return color; }

	public ItemStack getIcon() {
		return fellowshipIcon;
	}

	public String getLastQuestDate() {return lastQuestDate;}

	public List<UUID> getMemberUUIDs() {
		return memberUUIDs;
	}

	public String getName() {
		return fellowshipName;
	}

	public UUID getOwner() {
		return ownerUUID;
	}

	public int getXp() { return xp; }

	public int getPlayerCount() {
		return memberUUIDs.size() + 1;
	}

	public boolean getPreventHiredFriendlyFire() {
		return preventHiredFF;
	}

	public boolean getPreventPVP() {
		return preventPVP;
	}

	public byte[] getQuestData() {
		return questData;
	}

	public boolean getShowMapLocations() {
		return showMapLocations;
	}

	public Set<UUID> getWaypointSharerUUIDs() {
		return waypointSharerUUIDs;
	}

	public boolean hasMember(UUID player) {
		return memberUUIDs.contains(player);
	}

	public GuildInventory getInventory() {
		return inventory;
	}

	public boolean isAdmin(UUID player) {
		return hasMember(player) && adminUUIDs.contains(player);
	}

	public boolean isDisbanded() {
		return disbanded;
	}

	public boolean isOwner(UUID player) {
		return ownerUUID.equals(player);
	}

	public boolean isWaypointSharer(UUID player) {
		return waypointSharerUUIDs.contains(player);
	}


	public void onInventoryChanged(GuildInventory inventory) {

		NBTTagCompound inventoryData = new NBTTagCompound();
		inventory.writeToNBT(inventoryData);
		PacketSyncGuildStorage packet = new PacketSyncGuildStorage(inventoryData);
		PacketDispatcher.sendToAll(packet);
	}

	public void load(NBTTagCompound fsData) {
		disbanded = fsData.getBoolean("Disbanded");
		if (fsData.hasKey("isUpgraded")) {
			isUpgraded = fsData.getBoolean("isUpgraded");
		}
		level = fsData.getInteger("level");
		xp = fsData.getInteger("xp");
		color = fsData.getInteger("color");
		questData = fsData.getByteArray("quests");
		quests = getData(questData);
		if (fsData.hasKey("faction")) {
			faction = fsData.getString("faction");
		}
		if (fsData.hasKey("Inventory")) {
			inventory.readFromNBT(fsData.getCompoundTag("Inventory"));
		}
		if (fsData.hasKey("lastQuestDate")) {
			lastQuestDate = fsData.getString("lastQuestDate");
		}
		if (fsData.hasKey("Owner")) {
			ownerUUID = UUID.fromString(fsData.getString("Owner"));
		}
		memberUUIDs.clear();
		adminUUIDs.clear();
		NBTTagList memberTags = fsData.getTagList("Members", 10);
		for (int i = 0; i < memberTags.tagCount(); ++i) {
			NBTTagCompound nbt = memberTags.getCompoundTagAt(i);
			UUID member = UUID.fromString(nbt.getString("Member"));
			if (member == null) {
				continue;
			}
			memberUUIDs.add(member);
			if (!nbt.hasKey("Admin") || !nbt.getBoolean("Admin")) {
				continue;
			}
			adminUUIDs.add(member);
		}
		waypointSharerUUIDs.clear();
		NBTTagList waypointSharerTags = fsData.getTagList("WaypointSharers", 8);
		for (int i = 0; i < waypointSharerTags.tagCount(); ++i) {
			UUID waypointSharer = UUID.fromString(waypointSharerTags.getStringTagAt(i));
			if (waypointSharer == null || !containsPlayer(waypointSharer)) {
				continue;
			}
			waypointSharerUUIDs.add(waypointSharer);
		}
		if (fsData.hasKey("Name")) {
			fellowshipName = fsData.getString("Name");
		}
		if (fsData.hasKey("Icon")) {
			NBTTagCompound itemData = fsData.getCompoundTag("Icon");
			fellowshipIcon = ItemStack.loadItemStackFromNBT(itemData);
		}
		if (fsData.hasKey("PreventPVP")) {
			preventPVP = fsData.getBoolean("PreventPVP");
		}
		if (fsData.hasKey("PreventPVP")) {
			preventHiredFF = fsData.getBoolean("PreventHiredFF");
		}
		if (fsData.hasKey("ShowMap")) {
			showMapLocations = fsData.getBoolean("ShowMap");
		}
		validate();
		doneRetroactiveWaypointSharerCheck = fsData.getBoolean("DoneRetroactiveWaypointSharerCheck");
	}

	public void markIsWaypointSharer(UUID player, boolean flag) {
		if (containsPlayer(player)) {
			if (flag && !waypointSharerUUIDs.contains(player)) {
				waypointSharerUUIDs.add(player);
				markDirty();
			} else if (!flag && waypointSharerUUIDs.contains(player)) {
				waypointSharerUUIDs.remove(player);
				markDirty();
			}
		}
	}

	public boolean needsSave() {
		return needsSave;
	}

	public void removeMember(UUID player) {
		if (memberUUIDs.contains(player)) {
			memberUUIDs.remove(player);
			if (adminUUIDs.contains(player)) {
				adminUUIDs.remove(player);
			}
			if (waypointSharerUUIDs.contains(player)) {
				waypointSharerUUIDs.remove(player);
			}
			LOTRLevelData.getData(player).removeFellowship(this);
			updateForAllMembers(new FellowshipUpdateType.RemoveMember(player));
			markDirty();
		}
	}

	public void save(NBTTagCompound fsData) {
		System.out.println("save");
		NBTTagCompound inventoryCompound = new NBTTagCompound();
		inventory.writeToNBT(inventoryCompound);
		fsData.setTag("Inventory", inventoryCompound);
		fsData.setBoolean("Disbanded", disbanded);
		fsData.setInteger("level", level);
		fsData.setBoolean("isUpgraded", isUpgraded);
		fsData.setInteger("xp", xp);
		fsData.setInteger("color", color);
		fsData.setByteArray("quests", serializeData(quests));
		if (faction != null) {
			fsData.setString("faction", faction);
		}
		if (lastQuestDate != null) {
			fsData.setString("lastQuestDate", lastQuestDate);
		}
		if (ownerUUID != null) {
			fsData.setString("Owner", ownerUUID.toString());
		}
		NBTTagList memberTags = new NBTTagList();
		for (UUID member : memberUUIDs) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Member", member.toString());
			if (adminUUIDs.contains(member)) {
				nbt.setBoolean("Admin", true);
			}
			memberTags.appendTag(nbt);
		}
		fsData.setTag("Members", memberTags);
		NBTTagList waypointSharerTags = new NBTTagList();
		for (UUID waypointSharer : waypointSharerUUIDs) {
			waypointSharerTags.appendTag(new NBTTagString(waypointSharer.toString()));
		}
		fsData.setTag("WaypointSharers", waypointSharerTags);
		if (fellowshipName != null) {
			fsData.setString("Name", fellowshipName);
		}
		if (fellowshipIcon != null) {
			NBTTagCompound itemData = new NBTTagCompound();
			fellowshipIcon.writeToNBT(itemData);
			fsData.setTag("Icon", itemData);
		}
		fsData.setBoolean("PreventPVP", preventPVP);
		fsData.setBoolean("PreventHiredFF", preventHiredFF);
		fsData.setBoolean("ShowMap", showMapLocations);
		fsData.setBoolean("DoneRetroactiveWaypointSharerCheck", doneRetroactiveWaypointSharerCheck);
		needsSave = false;
	}

	public void sendFellowshipMessage(EntityPlayerMP sender, String message) {
		if (sender.func_147096_v() == EntityPlayer.EnumChatVisibility.HIDDEN) {
			ChatComponentTranslation msgCannotSend = new ChatComponentTranslation("chat.cannotSend");
			msgCannotSend.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.playerNetServerHandler.sendPacket(new S02PacketChat(msgCannotSend));
		} else {
			sender.func_143004_u();
			message = StringUtils.normalizeSpace(message);
			if (StringUtils.isBlank(message)) {
				return;
			}
			for (int i = 0; i < message.length(); ++i) {
				if (ChatAllowedCharacters.isAllowedCharacter(message.charAt(i))) {
					continue;
				}
				sender.playerNetServerHandler.kickPlayerFromServer("Illegal characters in chat");
				return;
			}
			ClickEvent fMsgClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fmsg \"" + getName() + "\" ");
			IChatComponent msgComponent = ForgeHooks.newChatWithLinks(message);
			msgComponent.getChatStyle().setColor(EnumChatFormatting.YELLOW);
			IChatComponent senderComponent = sender.func_145748_c_();
			senderComponent.getChatStyle().setChatClickEvent(fMsgClickEvent);
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("chat.type.text", senderComponent, "");
			chatComponent = ForgeHooks.onServerChatEvent(sender.playerNetServerHandler, message, chatComponent);
			if (chatComponent == null) {
				return;
			}
			chatComponent.appendSibling(msgComponent);
			ChatComponentTranslation fsComponent = new ChatComponentTranslation("commands.lotr.fmsg.fsPrefix", getName());
			fsComponent.getChatStyle().setColor(EnumChatFormatting.YELLOW);
			fsComponent.getChatStyle().setChatClickEvent(fMsgClickEvent);
			ChatComponentTranslation fullChatComponent = new ChatComponentTranslation("%s %s", fsComponent, chatComponent);
			MinecraftServer server = MinecraftServer.getServer();
			server.addChatMessage(fullChatComponent);
			S02PacketChat packetChat = new S02PacketChat(fullChatComponent, false);
			for (Object player : server.getConfigurationManager().playerEntityList) {
				EntityPlayerMP entityplayer = (EntityPlayerMP) player;
				if (!containsPlayer(entityplayer.getUniqueID())) {
					continue;
				}
				entityplayer.playerNetServerHandler.sendPacket(packetChat);
			}
		}
	}

	public void sendNotification(EntityPlayer entityplayer, String key, Object... args) {
		ChatComponentTranslation message = new ChatComponentTranslation(key, args);
		message.getChatStyle().setColor(EnumChatFormatting.YELLOW);
		entityplayer.addChatMessage(message);
		LOTRPacketFellowshipNotification packet = new LOTRPacketFellowshipNotification(message);
		LOTRPacketHandler.networkWrapper.sendTo((IMessage) packet, (EntityPlayerMP) entityplayer);
	}

	public void setAdmin(UUID player, boolean flag) {
		if (memberUUIDs.contains(player)) {
			if (flag && !adminUUIDs.contains(player)) {
				adminUUIDs.add(player);
				updateForAllMembers(new FellowshipUpdateType.SetAdmin(player));
				markDirty();
			} else if (!flag && adminUUIDs.contains(player)) {
				adminUUIDs.remove(player);
				updateForAllMembers(new FellowshipUpdateType.RemoveAdmin(player));
				markDirty();
			}
		}
	}

	public void setDisbandedAndRemoveAllMembers() {
		disbanded = true;
		markDirty();
		ArrayList<UUID> copyMemberIDs = new ArrayList<>(memberUUIDs);
		for (UUID player : copyMemberIDs) {
			removeMember(player);
		}
	}

	public void setIcon(ItemStack itemstack) {
		fellowshipIcon = itemstack;
		updateForAllMembers(new FellowshipUpdateType.ChangeIcon());
		markDirty();
	}

	public void setColor(int col) {
		color = col;
		updateForAllMembers(new FellowshipUpdateType.ChangeColor());
		markDirty();
	}

	public void setName(String name) {
		fellowshipName = name;
		updateForAllMembers(new FellowshipUpdateType.Rename());
		markDirty();
	}

	public void setLastQuestDate(String d) {
		lastQuestDate = d;
		updateForAllMembers(new FellowshipUpdateType.SetLastQuestDate());
		markDirty();
	}

	public void setFaction(String f) {
		faction = f;
		updateForAllMembers(new FellowshipUpdateType.changeFaction());
		markDirty();
	}

	public void SetXP(int i) {
		xp = i;
		updateForAllMembers(new FellowshipUpdateType.SetXP());
		markDirty();

		if(getLevel() != LevelRegister.Flevels.size() && xp >= LevelRegister.getLevelbyNum(getLevel() + 1).getXpToReach() ) {
			setLevel(getLevel() + 1);
		}
	}

	public void SetQuests(byte[] q) {
		questData = q;
		updateForAllMembers(new FellowshipUpdateType.SetQuests());
		markDirty();
	}


	public void setLevel(int i) {
		level = i;
		updateForAllMembers(new FellowshipUpdateType.SetLevel());
		markDirty();

		SetXP(0);
	}

	public void setUpgraded(boolean upgraded) {
		this.isUpgraded = upgraded;
		updateForAllMembers(new FellowshipUpdateType.SetUpgraded());
		markDirty();
	}



	public void setOwner(UUID owner) {
		UUID prevOwner = ownerUUID;
		if (prevOwner != null && !memberUUIDs.contains(prevOwner)) {
			memberUUIDs.add(0, prevOwner);
		}
		ownerUUID = owner;
		if (memberUUIDs.contains(owner)) {
			memberUUIDs.remove(owner);
		}
		if (adminUUIDs.contains(owner)) {
			adminUUIDs.remove(owner);
		}
		LOTRLevelData.getData(ownerUUID).addFellowship(this);
		updateForAllMembers(new FellowshipUpdateType.SetOwner(ownerUUID));
		markDirty();
	}

	public void setPreventHiredFriendlyFire(boolean flag) {
		preventHiredFF = flag;
		updateForAllMembers(new FellowshipUpdateType.ToggleHiredFriendlyFire());
		markDirty();
	}

	public void setPreventPVP(boolean flag) {
		preventPVP = flag;
		updateForAllMembers(new FellowshipUpdateType.TogglePvp());
		markDirty();
	}

	public void setShowMapLocations(boolean flag) {
		showMapLocations = flag;
		updateForAllMembers(new FellowshipUpdateType.ToggleShowMapLocations());
		markDirty();
	}

	public void updateForAllMembers(FellowshipUpdateType updateType) {
		for (UUID player : getAllPlayerUUIDs()) {
			LOTRLevelData.getData(player).updateFellowship(this, updateType);
		}
	}

	public void validate() {
		if (fellowshipUUID == null) {
			fellowshipUUID = UUID.randomUUID();
		}
		if (ownerUUID == null) {
			ownerUUID = UUID.randomUUID();
		}
	}
}
