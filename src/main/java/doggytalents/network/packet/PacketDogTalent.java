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
public class PacketDogTalent extends IPacket {

	public int entityId;
	public String talentId;
	
	public PacketDogTalent() {}
	public PacketDogTalent(int entityId, String talentId) {
		this();
		this.entityId = entityId;
		this.talentId = talentId;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
		this.talentId = data.readUTF();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.entityId);
		dos.writeUTF(this.talentId);
	}

	@Override
	public void execute(EntityPlayer player) {
		Entity target = player.worldObj.getEntityByID(this.entityId);
        
        if(!(target instanceof EntityDog))
        	return;
        
		EntityDog dog = (EntityDog)target;
        
		dog.talents.setLevel(this.talentId, dog.talents.getLevel(this.talentId) + 1);
	}

}
