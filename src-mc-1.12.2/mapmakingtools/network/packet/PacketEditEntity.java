package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

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
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		player.openGui(MapMakingTools.INSTANCE, CommonProxy.ID_FILTER_ENTITY, player.world, this.entityId, 0, 0);
	}

}
