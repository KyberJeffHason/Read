package lotr.common.fellowship;

import java.util.*;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import lotr.common.*;
import lotr.common.network.*;

public interface FellowshipUpdateType {
	IMessage createUpdatePacket(LOTRPlayerData var1, LOTRFellowship var2);

	List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship var1);

	public static class AddMember implements FellowshipUpdateType {
		public UUID memberID;

		public AddMember(UUID member) {
			memberID = member;
		}

		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.AddMember(fs, memberID);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return ImmutableList.of(memberID);
		}
	}

	public static class ChangeIcon implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.ChangeIcon(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class Full implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowship(pd, fs, false);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return fs.getAllPlayerUUIDs();
		}
	}

	public static class RemoveAdmin implements FellowshipUpdateType {
		public UUID adminID;

		public RemoveAdmin(UUID admin) {
			adminID = admin;
		}

		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.RemoveAdmin(fs, adminID, fs.isAdmin(pd.getPlayerUUID()));
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class RemoveMember implements FellowshipUpdateType {
		public UUID memberID;

		public RemoveMember(UUID member) {
			memberID = member;
		}

		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.RemoveMember(fs, memberID);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return ImmutableList.of(memberID);
		}
	}

	public static class Rename implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.Rename(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class SetUpgraded implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.SetUpgraded(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class ChangeColor implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.ChangeColor(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class SetLastQuestDate implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.SetLastQuestDate(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class SetQuests implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.SetQuests(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}


	public static class SetXP implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.SetXP(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class SetLevel implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.SetLevel(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class changeFaction implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.changeFaction(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class SetAdmin implements FellowshipUpdateType {
		public UUID adminID;

		public SetAdmin(UUID admin) {
			adminID = admin;
		}

		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.SetAdmin(fs, adminID, fs.isAdmin(pd.getPlayerUUID()));
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class SetOwner implements FellowshipUpdateType {
		public UUID ownerID;

		public SetOwner(UUID owner) {
			ownerID = owner;
		}

		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.SetOwner(fs, ownerID, fs.isOwner(pd.getPlayerUUID()));
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return ImmutableList.of(ownerID);
		}
	}

	public static class ToggleHiredFriendlyFire implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.ToggleHiredFriendlyFire(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class TogglePvp implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.TogglePvp(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class ToggleShowMapLocations implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.ToggleShowMap(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

	public static class UpdatePlayerTitle implements FellowshipUpdateType {
		public UUID playerID;
		public LOTRTitle.PlayerTitle playerTitle;

		public UpdatePlayerTitle(UUID player, LOTRTitle.PlayerTitle title) {
			playerID = player;
			playerTitle = title;
		}

		@Override
		public IMessage createUpdatePacket(LOTRPlayerData pd, LOTRFellowship fs) {
			return new LOTRPacketFellowshipPartialUpdate.UpdatePlayerTitle(fs, playerID, playerTitle);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(LOTRFellowship fs) {
			return null;
		}
	}

}
