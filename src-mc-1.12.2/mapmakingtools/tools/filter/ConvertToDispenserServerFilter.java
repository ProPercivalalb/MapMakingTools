package mapmakingtools.tools.filter;

import mapmakingtools.api.filter.FilterServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class ConvertToDispenserServerFilter extends FilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile instanceof TileEntityDropper)
			return true;
		return false;
	}
}
