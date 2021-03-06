package mapmakingtools.tools;

import mapmakingtools.lib.Constants;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PlayerAccess {

	public static boolean canEdit(EntityPlayer player) {
		if(player == null)
			return false;

		boolean isCreativeMode = !Constants.HAS_TO_BE_CREATIVE || player.abilities.isCreativeMode;
		boolean isOpped = !Constants.HAS_TO_BE_OPPED || true;// TODO || player.canUseCommand(3, "mmt");

		return isCreativeMode && isOpped;
	}
	
	public static boolean canSeeBlockIdHelper(EntityPlayer player) {
		return canEdit(player) && Constants.SHOULD_SHOW_BLOCK_ID_HELPER;
	}
}
