package doggytalents.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import doggytalents.entity.EntityDog;
import doggytalents.network.IPacket;

/**
 * @author ProPercivalalb
 */
public class PacketDogObey extends IPacket {

	public int entityId;
	public boolean obey;
	
	public PacketDogObey() {}
	public PacketDogObey(int entityId, boolean obey) {
		this();
		this.entityId = entityId;
		this.obey = obey;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
		this.obey = data.readBoolean();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.entityId);
		dos.writeBoolean(this.obey);
	}

	@Override
	public void execute(EntityPlayer player) {
		Entity target = player.worldObj.getEntityByID(this.entityId);
        if(!(target instanceof EntityDog))
        	return;
        
        EntityDog dog = (EntityDog)target;
        
		dog.setWillObeyOthers(this.obey);
	}

}
