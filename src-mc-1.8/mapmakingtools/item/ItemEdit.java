package mapmakingtools.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class ItemEdit extends Item {
	
    public ItemEdit() {
    	this.setCreativeTab(CreativeTabs.tabMisc);
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
    	par3List.add(new ItemStack(item, 1, 0));
    	par3List.add(new ItemStack(item, 1, 1));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debug) {
    	
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	if(stack.getMetadata() == 1) 
    		return StatCollector.translateToLocal("item.mapmakingtools.wrench.name");
    	else
    		return StatCollector.translateToLocal("item.mapmakingtools.edit_item.name");
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if(stack != null && stack.getMetadata() == 0)
        	return true;
        
        return false;
    }
}
