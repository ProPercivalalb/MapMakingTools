package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.NumberParse;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class PacketCreeperProperties extends IPacketPos {

	public String fuseTime, explosionRadius;
	public int minecartIndex;
	
	public PacketCreeperProperties() {}
	public PacketCreeperProperties(BlockPos pos, String fuseTime, String explosionRadius, int minecartIndex) {
		super(pos);
		this.fuseTime = fuseTime;
		this.explosionRadius = explosionRadius;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.fuseTime = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.explosionRadius = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeString(this.fuseTime);
		packetbuffer.writeString(this.explosionRadius);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			

			if(!NumberParse.areIntegers(this.fuseTime, this.explosionRadius)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.creeperproperties.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return;
			}
			
			int fuseTimeNO = NumberParse.getInteger(this.fuseTime);
			int explosionRadiusNO = NumberParse.getInteger(this.explosionRadius);
			
			SpawnerUtil.setCreeperFuse(spawner.getSpawnerBaseLogic(), fuseTimeNO, this.minecartIndex);
			SpawnerUtil.setCreeperExplosionRadius(spawner.getSpawnerBaseLogic(), explosionRadiusNO, this.minecartIndex);
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.creeperproperties.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

}