package doggytalents.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import doggytalents.DoggyTalentsMod;
import doggytalents.ModBlocks;
import doggytalents.api.DoggyTalentsAPI;
import doggytalents.api.registry.DogBedRegistry;
import doggytalents.entity.EntityDog;
import doggytalents.tileentity.TileEntityDogBed;

/**
 * @author ProPercivalalb
 */
public class BlockDogBed extends BlockContainer {

	public BlockDogBed() {
		super(Material.wood);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setStepSound(Block.soundTypeWood);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.6F, 1.0F);
		this.setCreativeTab(DoggyTalentsAPI.CREATIVE_TAB);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDogBed();
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack) {
	    int facingDirection = MathHelper.floor_double((double)(entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	    world.setBlockMetadataWithNotify(x, y, z, facingDirection, 2);

	    if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("doggytalents")) {
	    	NBTTagCompound tag = stack.stackTagCompound.getCompoundTag("doggytalents");
	    	
	    	String casingId = tag.getString("casingId");
	    	if(DogBedRegistry.CASINGS.isValidId(casingId)) 
	    		((TileEntityDogBed)world.getTileEntity(x, y, z)).setCasingId(casingId);
	    	
	    	String beddingId = tag.getString("beddingId");
	    	if(DogBedRegistry.BEDDINGS.isValidId(beddingId))
	    		((TileEntityDogBed)world.getTileEntity(x, y, z)).setBeddingId(beddingId);
	    }
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int x, int y, int z, int side) {
        if(side == 0) {
        	Block block = par1IBlockAccess.getBlock(x, y - 1, z);
        	if(par1IBlockAccess instanceof World)
        		return block.isSideSolid((World)par1IBlockAccess, x, y - 1, z, ForgeDirection.UP);
        	else
        		return block.isBlockSolid(par1IBlockAccess, x, y - 1, z, 1);
        }
		return true;
    }
	
	@Override
	public Item getItemDropped(int meta, Random par2Random, int fortune) {
	    return Item.getItemById(-1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int sideHit = target.sideHit;
		
		TileEntity tile = worldObj.getTileEntity(x, y, z);
		IIcon icon = Blocks.planks.getIcon(0, 0);
		if(tile instanceof TileEntityDogBed) {
			TileEntityDogBed dogBed = (TileEntityDogBed)tile;
			icon = this.getIconSafe(DogBedRegistry.CASINGS.getIcon(dogBed.getCasingId(), sideHit));
		}
		
		Block block = ModBlocks.dogBed;
		float b = 0.1F;
		double px = x + worldObj.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (b * 2.0F)) + b + block.getBlockBoundsMinX();
		double py = y + worldObj.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (b * 2.0F)) + b + block.getBlockBoundsMinY();
		double pz = z + worldObj.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (b * 2.0F)) + b + block.getBlockBoundsMinZ();

		if (sideHit == 0)
			py = (double) y + block.getBlockBoundsMinY() - (double) b;
		else if (sideHit == 1)
			py = (double) y + block.getBlockBoundsMaxY() + (double) b;
		else if (sideHit == 2)
			pz = (double) z + block.getBlockBoundsMinZ() - (double) b;
		else if (sideHit == 3)
			pz = (double) z + block.getBlockBoundsMaxZ() + (double) b;
		else if (sideHit == 4)
			px = (double) x + block.getBlockBoundsMinX() - (double) b;
		else if (sideHit == 5)
			px = (double) x + block.getBlockBoundsMaxX() + (double) b;

		EntityDiggingFX fx = new EntityDiggingFX(worldObj, px, py, pz, 0.0D, 0.0D, 0.0D, block, sideHit, worldObj.getBlockMetadata(x, y, z));
		fx.setParticleIcon(icon);
		effectRenderer.addEffect(fx.applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World worldObj, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		TileEntity target = worldObj.getTileEntity(x, y, z);

		byte its = 4;
		for (int i = 0; i < its; ++i) {
			for (int j = 0; j < its; ++j) {
				for (int k = 0; k < its; ++k) {
					double px = x + (i + 0.5D) / (double)its;
					double py = y + (j + 0.5D) / (double)its;
					double pz = z + (k + 0.5D) / (double)its;
					int random = worldObj.rand.nextInt(6);
					IIcon icon = Blocks.planks.getIcon(0, 0);
					if(target instanceof TileEntityDogBed) {
						TileEntityDogBed dogBed = (TileEntityDogBed)target;
						icon = this.getIconSafe(DogBedRegistry.CASINGS.getIcon(dogBed.getCasingId(), random));
					}
					EntityDiggingFX fx = new EntityDiggingFX(worldObj, px, py, pz, px - x - 0.5D, py - y - 0.5D, pz - z - 0.5D, ModBlocks.dogBed, random, meta);
					fx.setParticleIcon(icon);
					effectRenderer.addEffect(fx.applyColourMultiplier(x, y, z));
				}
			}
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconSafe(IIcon icon) {
	    if (icon == null)
	        icon = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");

	    return icon;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
		TileEntity tile = blockAccess.getTileEntity(x, y, z);
		if(tile instanceof TileEntityDogBed) {
			TileEntityDogBed dogBed = (TileEntityDogBed)tile;
			return DogBedRegistry.CASINGS.getIcon(dogBed.getCasingId(), side);
		}
		return null;
    }
	
	@Override
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
		if(par7Entity instanceof EntityDog) {
	       
		}
		else {
			super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		}
    }
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		TileEntity target = world.getTileEntity(x, y, z);
		if(!(target instanceof TileEntityDogBed))
			return super.removedByPlayer(world, player, x, y, z, willHarvest);
		TileEntityDogBed dogBed = (TileEntityDogBed)target;
		
		ItemStack stack = DogBedRegistry.createItemStack(dogBed.getCasingId(), dogBed.getBeddingId());
		
		boolean flag = super.removedByPlayer(world, player, x, y, z, willHarvest);
		if(player == null || !player.capabilities.isCreativeMode)
			this.dropBlockAsItem(world, x, y, z, stack);
		
		return flag;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
		
		if(!(tile instanceof TileEntityDogBed))
			return null;
		TileEntityDogBed dogBed = (TileEntityDogBed)tile;
		
		return DogBedRegistry.createItemStack(dogBed.getCasingId(), dogBed.getBeddingId());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {}

	@Override
	public boolean isOpaqueCube() {
        return false;
    }
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
	    return DoggyTalentsMod.proxy.RENDER_ID_DOG_BED;
	}
	
	@Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (!super.canPlaceBlockAt(world, x, y, z))
            return false;
        else
            return canBlockStay(world, x, y, z);
    }
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!this.canBlockStay(world, x, y, z))
			this.removedByPlayer(world, null, x, y, z, false);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return block != null && block.isSideSolid(world, x, y - 1, z, ForgeDirection.UP);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List stackList) {
		for(String casingId : DogBedRegistry.CASINGS.getKeys())
			stackList.add(DogBedRegistry.createItemStack(casingId, Block.blockRegistry.getNameForObject(Blocks.wool) + ".0"));
    }
}
