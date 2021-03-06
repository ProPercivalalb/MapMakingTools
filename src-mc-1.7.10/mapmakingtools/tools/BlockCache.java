package mapmakingtools.tools;


import java.io.IOException;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import mapmakingtools.api.enums.MovementType;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * @author ProPercivalalb
 * This class is based off {@link net.minecraftforge.common.util.BlockSnapshot}
 */
public class BlockCache {

	public final BlockPos playerPos;
    public final BlockPos pos;
    public final int dimId;
    private final NBTTagCompound nbt;
    public final UniqueIdentifier blockIdentifier;
    public final Block block;
    public final int meta;

    public final World world;

    private BlockCache(BlockPos playerPos, World world, BlockPos pos, Block block, int meta) {
        this.playerPos = playerPos;
    	this.world = world;
        this.dimId = world.provider.dimensionId;
        this.pos = pos;
        this.block = block;
        this.blockIdentifier = GameRegistry.findUniqueIdentifierFor(this.block);
        this.meta = meta;
        TileEntity tileEntity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (tileEntity != null) {
            this.nbt = new NBTTagCompound();
            tileEntity.writeToNBT(this.nbt);
        }
        else 
        	this.nbt = null;
    }

    private BlockCache(BlockPos playerPos, int dimension, BlockPos pos, String modid, String blockName, int meta, NBTTagCompound nbt) {
    	this.playerPos = playerPos;
    	this.world = DimensionManager.getWorld(dimension);
    	this.dimId = dimension;
        this.pos = pos;
        this.block = Block.getBlockFromName(modid + ":" + blockName);
        this.blockIdentifier = new UniqueIdentifier(modid + ":" + blockName);
        this.meta = meta;
        this.nbt = nbt;
    }

    public static BlockCache createCache(EntityPlayer player, World world, BlockPos pos) {
        return new BlockCache(new BlockPos(player), world, pos, world.getBlock(pos.getX(), pos.getY(), pos.getZ()), world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()));
    }
    
    public static BlockCache createCache(BlockPos playerPos, World world, BlockPos pos) {
        return new BlockCache(playerPos, world, pos, world.getBlock(pos.getX(), pos.getY(), pos.getZ()), world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static BlockCache readFromNBT(NBTTagCompound tag) {
        NBTTagCompound nbt = tag.getBoolean("hasTE") ? null : tag.getCompoundTag("tileEntity");

        return new BlockCache(
        		BlockPos.fromLong(tag.getLong("playerPos")),
        		tag.getInteger("dimension"),
                BlockPos.fromLong(tag.getLong("blockPos")),
                tag.getString("blockMod"),
                tag.getString("blockName"),
                tag.getInteger("metadata"),
                nbt);
    }
    
    public static BlockCache readFromPacketBuffer(PacketBuffer packetbuffer) throws IOException {
        return new BlockCache(
        		BlockPos.fromLong(packetbuffer.readLong()),
        		packetbuffer.readInt(),
        		BlockPos.fromLong(packetbuffer.readLong()),
                packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4),
                packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4),
                packetbuffer.readInt(),
                packetbuffer.readNBTTagCompoundFromBuffer());
    }

    public boolean restore(boolean applyPhysics) {
        return this.restoreToLocation(this.world, this.pos, applyPhysics);
    }

    public boolean restoreToLocation(World world, BlockPos pos, boolean applyPhysics) {

        world.setBlock(pos.getX(), pos.getY(), pos.getZ(), this.block, this.meta, applyPhysics ? 3 : 2);
        world.markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
        if (this.nbt != null) {
        	TileEntity tileEntity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            if (tileEntity != null) {
                tileEntity.readFromNBT(this.nbt);
                tileEntity.xCoord = pos.getX();
	            tileEntity.yCoord = pos.getY();
	            tileEntity.zCoord = pos.getZ();
            }
        }
        
        return true;
    }
    
    public BlockCache restoreRelativeToRotated(PlayerData data, MovementType movementType) { 
		BlockPos newPos = this.pos.subtract(this.playerPos);
		BlockPos newPlayerPos = new BlockPos(data.getPlayer());
		
		if(movementType.equals(MovementType._090_))
			newPos = new BlockPos(-newPos.getZ(), newPos.getY(), newPos.getX());
		else if(movementType.equals(MovementType._180_))
			newPos = new BlockPos(-newPos.getX(), newPos.getY(), -newPos.getZ());
		else if(movementType.equals(MovementType._270_))
			newPos = new BlockPos(newPos.getZ(), newPos.getY(), -newPos.getX());
		
		newPos = newPos.add(newPlayerPos);
		
		BlockCache bse = BlockCache.createCache(data.getPlayer(), data.getPlayerWorld(), newPos);
		
		if(!RotationLoader.onRotation(data.getPlayerWorld(), newPos, this.blockIdentifier, this.block, this.meta, movementType))
			data.getPlayerWorld().setBlock(pos.getX(), pos.getY(), pos.getZ(), this.block, this.meta, 2);
		
		data.getPlayerWorld().markBlockForUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
	    if (this.nbt != null) {
	    	TileEntity tileEntity = data.getPlayerWorld().getTileEntity(newPos.getX(), newPos.getY(), newPos.getZ());
	        if (tileEntity != null) {
	            tileEntity.readFromNBT(this.nbt);
	            tileEntity.xCoord = newPos.getX();
	            tileEntity.yCoord = newPos.getY();
	            tileEntity.zCoord = newPos.getZ();
	        }
	    }
	    
	    return bse;
	}
    
    public BlockCache restoreRelative(PlayerData data) { 
		BlockPos newPos = this.pos.subtract(this.playerPos);
		BlockPos newPlayerPos = new BlockPos(data.getPlayer());
		newPos = newPos.add(newPlayerPos);

		BlockCache bse = BlockCache.createCache(data.getPlayer(), data.getPlayerWorld(), newPos);
		
		this.restoreToLocation(data.getPlayerWorld(), newPos, false);
		
		return bse;
	}
    
    public BlockCache restoreRelative(World world, BlockPos pos) { 
		BlockPos newPos = this.pos.subtract(this.playerPos);
		BlockPos newPlayerPos = pos;
		newPos = newPos.add(newPlayerPos);

		BlockCache bse = BlockCache.createCache(pos, world, newPos);
		
		this.restoreToLocation(world, newPos, false);
		
		return bse;
	}
	
	public BlockCache restoreRelativeToFlipped(PlayerData data, MovementType movementType) { 
		BlockPos newPos = this.pos;
		
		if(movementType.equals(MovementType._X_))
			newPos = new BlockPos(data.getMaxX() - (this.pos.getX() - data.getMinX()), this.pos.getY(), this.pos.getZ());
		else if(movementType.equals(MovementType._Z_))
			newPos = new BlockPos(this.pos.getX(), this.pos.getY(), data.getMaxZ() - (this.pos.getZ() - data.getMinZ()));
		else if(movementType.equals(MovementType._Y_))
			newPos = new BlockPos(this.pos.getX(), data.getMaxY() - (this.pos.getY() - data.getMinY()), this.pos.getZ());
	
		BlockCache bse = BlockCache.createCache(data.getPlayer(), data.getPlayerWorld(), newPos);
		
		if(!RotationLoader.onRotation(data.getPlayerWorld(), newPos, this.blockIdentifier, this.block, this.meta, movementType))
			data.getPlayerWorld().setBlock(pos.getX(), pos.getY(), pos.getZ(), this.block, this.meta, 2);
		
		data.getPlayerWorld().markBlockForUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
	    if (this.nbt != null) {
	    	TileEntity tileEntity = data.getPlayerWorld().getTileEntity(newPos.getX(), newPos.getY(), newPos.getZ());
	        if (tileEntity != null) {
	            tileEntity.readFromNBT(this.nbt);
	            tileEntity.xCoord = newPos.getX();
	            tileEntity.yCoord = newPos.getY();
	            tileEntity.zCoord = newPos.getZ();
	        }
	    }
	    
	    return bse;
	}

    public void writeToNBT(NBTTagCompound compound) {
    	compound.setLong("playerPos", this.playerPos.toLong());
        compound.setString("blockMod", this.blockIdentifier.modId);
        compound.setString("blockName", this.blockIdentifier.name);
        compound.setLong("blockPos", this.pos.toLong());
        compound.setInteger("dimension", this.dimId);
        compound.setInteger("metadata", this.meta);

        compound.setBoolean("hasTE", this.nbt != null);

        if (this.nbt != null)
            compound.setTag("tileEntity", this.nbt);
    }
    
    public void writeToPacketBuffer(PacketBuffer packetbuffer) throws IOException {
    	packetbuffer.writeLong(this.playerPos.toLong());
		packetbuffer.writeInt(this.dimId);
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeStringToBuffer(this.blockIdentifier.modId);
		packetbuffer.writeStringToBuffer(this.blockIdentifier.name);
		packetbuffer.writeInt(this.meta);
		packetbuffer.writeNBTTagCompoundToBuffer(this.nbt);
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        BlockCache other = (BlockCache)obj;
        if (!this.pos.equals(other.pos))
            return false;
        if (this.meta != other.meta)
            return false;
        if (this.dimId != other.dimId)
            return false;
        if (this.nbt != other.nbt && (this.nbt == null || !this.nbt.equals(other.nbt)))
            return false;
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world)))
            return false;
        if (this.blockIdentifier != other.blockIdentifier && (this.blockIdentifier == null || !this.blockIdentifier.equals(other.blockIdentifier)))
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.pos.getX();
        hash = 73 * hash + this.pos.getY();
        hash = 73 * hash + this.pos.getZ();
        hash = 73 * hash + this.meta;
        hash = 73 * hash + this.dimId;
        hash = 73 * hash + (this.nbt != null ? this.nbt.hashCode() : 0);
        hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 73 * hash + (this.blockIdentifier != null ? this.blockIdentifier.hashCode() : 0);
        return hash;
    }
}