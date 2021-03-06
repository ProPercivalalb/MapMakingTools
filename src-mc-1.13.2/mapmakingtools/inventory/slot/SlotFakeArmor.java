package mapmakingtools.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author ProPercivalalb
 */
public class SlotFakeArmor extends SlotFake {

	public EntityPlayer player;
	public EntityEquipmentSlot armorType;
	
    public SlotFakeArmor(EntityPlayer playerIn, IInventory inventoryIn, int index, int xPosition, int yPosition, EntityEquipmentSlot armorType) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = playerIn;
        this.armorType = armorType;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
    	return stack.canEquip(armorType, player);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public String getSlotTexture() {
        return SlotArmor.ARMOR_SLOT_TEXTURES[armorType.getIndex()];
    }
}
