package mapmakingtools.tools.filter;

import mapmakingtools.api.filter.FilterServerInventory;
import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.inventory.slot.SlotArmor;
import mapmakingtools.inventory.slot.SlotFake;
import mapmakingtools.inventory.slot.SlotFakeArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author ProPercivalalb
 */
public class MobArmourServerFilter extends FilterServerInventory {

	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	
	@Override
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityLiving; 
	}
	
	@Override
	public void addSlots(IFilterContainer container) {
        for (int i = 0; i < 4; ++i) {
        	container.addSlotForFilter(new SlotArmor(container.getPlayer(), container.getPlayer().inventory, 36 + (3 - i), 130 + i * 18, 40, VALID_EQUIPMENT_SLOTS[i]));
        }
        container.addSlotForFilter(new SlotFake(getInventory(container), 0, 14, 39));
        container.addSlotForFilter(new SlotFake(getInventory(container), 1, 14, 66) {
            @OnlyIn(Dist.CLIENT)
            @Override
            public String getSlotTexture() {
                return "minecraft:item/empty_armor_slot_shield";
            }
        });
        for (int i = 0; i < 4; ++i) {
        	container.addSlotForFilter(new SlotFakeArmor(container.getPlayer(), getInventory(container), getInventory(container).getSizeInventory() - 1 - i, 40 + i * 18, 40, VALID_EQUIPMENT_SLOTS[i]));
        }
		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				container.addSlotForFilter(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 40 + j * 18, 70 + i * 18));
	        }
		}	

	    for (int i = 0; i < 9; ++i) {
	    	container.addSlotForFilter(new Slot(container.getPlayer().inventory, i, 40 + i * 18, 128));
	    }
	}
	
	@Override
	public ItemStack transferStackInSlot(IFilterContainer container, EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = (Slot)container.getInventorySlots().get(index);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	        EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
	        
	        boolean wasPhantomSlot = false;

	        if(entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)container.getInventorySlots().get(9 - entityequipmentslot.getIndex())).getHasStack()) {
	        	wasPhantomSlot = true;
                int i = 9 - entityequipmentslot.getIndex();

                if (!container.mergeItemStacks(itemstack1, i, i + 1, false))
                    return ItemStack.EMPTY;
            }
	        else if(entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.HAND) {
                if (!container.mergeItemStacks(itemstack1, 4, 6, false))
                    return ItemStack.EMPTY;
	        }
	        
	        if(itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if(itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;
	    }

	    return itemstack;
	}
	
	@Override
	public String getSaveId() {
		return "mobArmor";
	}
	
	@Override
	public IInventory createInventory() {
		return new InventoryBasic(new TextComponentString("Mob Armour"), 6);
	}
}
