package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketFillInventory extends IPacket {

	public int x, y, z;
	
	public PacketFillInventory() {}
	public PacketFillInventory(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof FillInventoryServerFilter) {
				
				FillInventoryServerFilter filterCurrent = (FillInventoryServerFilter)container.filterCurrent;
				if (tile instanceof IInventory) {
					
			    	for(int i = 0; i < ((IInventory)tile).getSizeInventory(); ++i) {
			    		ItemStack newStack = null;
			    		if(container.getSlot(0).getStack() != null) {
			    			newStack = container.getSlot(0).getStack().copy();
			    			newStack.stackSize = ((IPhantomSlot)container.getSlot(0)).isUnlimited() ? -1 : container.getSlot(0).getStack().stackSize;
			    		}
			    		((IInventory)tile).setInventorySlotContents(i, newStack);
			    	}
			    	ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.fillinventory.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName());
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
				}
			}
		}
	}

}
