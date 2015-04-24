package doggytalents.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import doggytalents.ModItems;

/**
 * @author ProPercivalalb
 */
public class CreativeTabDoggyTalents extends CreativeTabs {

	public CreativeTabDoggyTalents() {
		super("doggytalents");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ModItems.trainingTreat;
	}
}
