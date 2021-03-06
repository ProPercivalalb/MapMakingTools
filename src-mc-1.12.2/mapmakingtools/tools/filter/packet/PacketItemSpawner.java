package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.ItemSpawnerServerFilter;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketItemSpawner extends AbstractServerMessage {

	public int minecartIndex;
	
	public PacketItemSpawner() {}
	public PacketItemSpawner(int minecartIndex) {
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			if(container.filterCurrent instanceof ItemSpawnerServerFilter) {
				ItemSpawnerServerFilter filterCurrent = (ItemSpawnerServerFilter)container.filterCurrent;
				IInventory inventory = filterCurrent.getInventory(container); 
				
				if(SpawnerUtil.isSpawner(container)) {
					MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
				
					ItemStack item = inventory.getStackInSlot(0).copy();
					SpawnerUtil.setItemType(spawnerLogic, item, this.minecartIndex);
					
					if(container.getTargetType() == TargetType.BLOCK) {
						TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
		
						PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
						PacketUtil.sendTileEntityUpdateToWatching(spawner);
					}
					
					TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.changeitem.complete", item.isEmpty() ? "Nothing" : item.getDisplayName());
					chatComponent.getStyle().setItalic(true);
					player.sendMessage(chatComponent);
				}
			}
		}
	}
}
