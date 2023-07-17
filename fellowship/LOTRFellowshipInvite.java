package lotr.common.fellowship;

import java.util.UUID;

public class LOTRFellowshipInvite {
	public UUID fellowshipID;
	public UUID inviterID;

	public LOTRFellowshipInvite(UUID fs, UUID inviter) {
		fellowshipID = fs;
		inviterID = inviter;
	}
}
