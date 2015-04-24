package doggytalents.inventory;

import doggytalents.entity.EntityDog;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class InventoryPackPuppy implements IInventory {
	
	public ItemStack[] inventorySlots;
    private EntityDog dog;

    public InventoryPackPuppy(EntityDog dog) {
        this.dog = dog;
        this.inventorySlots = new ItemStack[this.getSizeInventory()];
    }

    @Override
	public int getSizeInventory()  {
		return 15;
	}

	@Override
	public ItemStack getStackInSlot(int var1)  {
		return this.inventorySlots[var1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		if (this.inventorySlots[par1] != null) {
            ItemStack var3;

            if (this.inventorySlots[par1].stackSize <= par2) {
                var3 = this.inventorySlots[par1];
                this.inventorySlots[par1] = null;
                this.markDirty();
                return var3;
            }
            else {
                var3 = this.inventorySlots[par1].splitStack(par2);

                if (this.inventorySlots[par1].stackSize == 0)
                {
                    this.inventorySlots[par1] = null;
                }

                this.markDirty();
                return var3;
            }
        }
        else {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.inventorySlots[par1] != null) {
            ItemStack var2 = this.inventorySlots[par1];
            this.inventorySlots[par1] = null;
            return var2;
        }
        else {
            return null;
        }
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)  {
		this.inventorySlots[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
	}

	@Override
	public String getInventoryName()  {
		return "container.packpuppy";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

    @Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
	
	public void readFromNBT(NBTTagCompound tagCompound) {
        NBTTagList nbttaglist = tagCompound.getTagList("packpuppyitems", 10);
        
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.inventorySlots.length)
                this.inventorySlots[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
        }
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventorySlots.length; ++i) {
            if (this.inventorySlots[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.inventorySlots[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        tagCompound.setTag("packpuppyitems", nbttaglist);
    }

	@Override
	public void markDirty() {
		
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}
}
