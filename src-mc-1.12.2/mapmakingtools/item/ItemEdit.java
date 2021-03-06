package mapmakingtools.item;

import java.util.List;

import mapmakingtools.tools.PlayerAccess;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class ItemEdit extends Item {
	
    public ItemEdit() {
    	this.setCreativeTab(CreativeTabs.MISC);
    	this.setMaxStackSize(1);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    	if(this.isInCreativeTab(tab)) {
    		items.add(new ItemStack(this, 1, 0));
    		items.add(new ItemStack(this, 1, 1));
    		items.add(new ItemStack(Blocks.COMMAND_BLOCK));
    		items.add(new ItemStack(Blocks.MOB_SPAWNER));
    		items.add(new ItemStack(Items.COMMAND_BLOCK_MINECART));
    		items.add(new ItemStack(Items.KNOWLEDGE_BOOK));
    		items.add(new ItemStack(Items.FIREWORKS));
    	}
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	
    }
    
    @Override
    public String getTranslationKey(ItemStack stack) {
    	if(stack.getMetadata() == 1) 
    		return "item.mapmakingtools.wrench";
    	else
    		return "item.mapmakingtools.edit_item";
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if(stack != null && stack.getMetadata() == 0 && PlayerAccess.canEdit(player))
        	return true;
        
        return false;
    }
}
