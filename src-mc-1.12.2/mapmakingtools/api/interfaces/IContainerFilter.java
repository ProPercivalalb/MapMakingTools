package mapmakingtools.api.interfaces;

import java.util.List;

import mapmakingtools.api.enums.TargetType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IContainerFilter extends IFilterBase {

	public void addSlot(Slot slot);
	public List<Slot> getInventorySlots();
	public boolean mergeItemStacks(ItemStack itemstack1, int j, int i, boolean b);

	public FilterServer getCurrentFilter();
}
