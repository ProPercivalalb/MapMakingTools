package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateEntity extends AbstractMessage {

	public int entityId;
	public NBTTagCompound tagCompound;
	public boolean onlyUpdate;
	
	public PacketUpdateEntity() {}
	public PacketUpdateEntity(Entity entity, boolean onlyUpdate) {
		this.entityId = entity.getEntityId();
		this.tagCompound = entity.serializeNBT();
		this.onlyUpdate = onlyUpdate;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.entityId = packetbuffer.readInt();
		this.tagCompound = packetbuffer.readCompoundTag();
		this.onlyUpdate = packetbuffer.readBoolean();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.entityId);
		packetbuffer.writeCompoundTag(this.tagCompound);
		packetbuffer.writeBoolean(this.onlyUpdate);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		Entity entity = player.world.getEntityByID(this.entityId);
		
		if(entity == null)
			return;
		
		FakeWorldManager.putEntity(entity, this.tagCompound);
		
		if(!this.onlyUpdate)
			PacketDispatcher.sendToServer(new PacketEditEntity(entity));
	}

}
