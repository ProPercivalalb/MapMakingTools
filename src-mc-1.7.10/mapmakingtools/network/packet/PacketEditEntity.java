package mapmakingtools.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.MapMakingTools;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketEditEntity extends AbstractServerMessage {

	public int entityId;
	
	public PacketEditEntity() {}
	public PacketEditEntity(Entity entity) {
		this.entityId = entity.getEntityId();
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.entityId = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.entityId);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		player.openGui(MapMakingTools.instance, CommonProxy.ID_FILTER_ENTITY, player.worldObj, this.entityId, 0, 0);
		return null;
	}

}
