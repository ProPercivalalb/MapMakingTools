package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.Numbers;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

public class PacketMobPosition extends AbstractServerMessage {

	public BlockPos pos;
	public String xPos, yPos, zPos;
	public boolean relative;
	public int minecartIndex;
	
	public PacketMobPosition() {}
	public PacketMobPosition(BlockPos pos, String xPos, String yPos, String zPos, boolean relative, int minecartIndex) {
		this.pos = pos;
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.relative = relative;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.xPos = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.yPos = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.zPos = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.relative = packetbuffer.readBoolean();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeString(this.xPos);
		packetbuffer.writeString(this.yPos);
		packetbuffer.writeString(this.zPos);
		packetbuffer.writeBoolean(this.relative);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!Numbers.areDoubles(this.xPos, this.yPos, this.zPos)) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobposition.notint");
				chatComponent.getStyle().setItalic(true);
				chatComponent.getStyle().setColor(TextFormatting.RED);
				player.sendMessage(chatComponent);
				return;
			}
			
			double xPosNO = Numbers.getDouble(this.xPos);
			double yPosNO = Numbers.getDouble(this.yPos);
			double zPosNO = Numbers.getDouble(this.zPos);
			if(this.relative) {
				xPosNO += this.pos.getX();
				yPosNO += this.pos.getY();
				zPosNO += this.pos.getZ();
			}
			
			SpawnerUtil.setPosition(spawner.getSpawnerBaseLogic(), xPosNO, yPosNO, zPosNO, this.minecartIndex);
			PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, pos, true), player);
			PacketUtil.sendTileEntityUpdateToWatching(spawner);
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobposition.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
