package doggytalents.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import doggytalents.DoggyTalentsMod;
import doggytalents.api.DoggyTalentsAPI;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

/**
 * @author ProPercivalalb
 **/
public class ItemRadar extends ItemMap {
	
	public ItemRadar() {
		this.setCreativeTab(DoggyTalentsAPI.CREATIVE_TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("doggytalents:radar");
	}
	
	@Override
	public MapData getMapData(ItemStack p_77873_1_, World p_77873_2_) {
        MapData mapdata = new MapData("radar");
        return mapdata;
    }
}
