package doggytalents.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import doggytalents.DoggyTalentsMod;
import doggytalents.api.DoggyTalentsAPI;
import doggytalents.entity.EntityDog;
import doggytalents.proxy.CommonProxy;
import doggytalents.tileentity.TileEntityFoodBowl;

/**
 * @author ProPercivalalb
 **/
public class BlockFoodBowl extends BlockContainer {
	
    public static IIcon top;
    public static IIcon side;
    public static IIcon bottom;
	
    public BlockFoodBowl() {
        super(Material.iron);
        this.setTickRandomly(true);
        this.setCreativeTab(DoggyTalentsAPI.CREATIVE_TAB);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F - 0.0625F, 0.5F, 1.0F - 0.0625F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int i, int j, int k) {
        int var1 = par1IBlockAccess.getBlockMetadata(i, j, k);
        float var2 = 0.0625F;
        float var3 = (float)(1 + var1 * 2) / 16F;
        float var4 = 0.5F;
        setBlockBounds(var3, 0.0F, var2, 1.0F - var2, var4, 1.0F - var2);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float var1 = 0.0625F;
        float var2 = 0.5F;
        this.setBlockBounds(var1, 0.0F, var1, 1.0F - var1, var2, 1.0F - var1);
    }

    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        List list = par1World.getEntitiesWithinAABB(EntityDog.class, AxisAlignedBB.getBoundingBox((float)par2, (double)(float)par3 + 0.5D, (float)par4, (float)(par2 + 1), (double)(float)par3 + 0.5D + 0.05000000074505806D, (float)(par4 + 1)).expand(5, 5, 5));

        if (list != null && list.size() > 0)
        {
            for (int l = 0; l < list.size(); l++)
            {
            	EntityDog entitydtdoggy = (EntityDog)list.get(l);
                //TODO entitydtdoggy.saveposition.setBowlX(par2);
              //TODO entitydtdoggy.saveposition.setBowlY(par3);
              //TODO entitydtdoggy.saveposition.setBowlZ(par4);
            }
        }
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int i, int j, int k)
    {
        int l = par1World.getBlockMetadata(i, j, k);
        float f = 0.0625F;
        float f1 = (float)(1 + l * 2) / 16F;
        float f2 = 0.5F;
        return AxisAlignedBB.getBoundingBox((float)i + f1, j, (float)k + f, (float)(i + 1) - f, ((float)j + f2) - f, (float)(k + 1) - f);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        int var1 = par1World.getBlockMetadata(par2, par3, par4);
        float var2 = 0.0625F;
        float var3 = (float)(1 + var1 * 2) / 16F;
        float var4 = 0.5F;
        return AxisAlignedBB.getBoundingBox((float)par2 + var3, par3, (float)par4 + var2, (float)(par2 + 1) - var2, (float)par3 + var4, (float)(par4 + 1) - var2);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
    	return side == 1 ? this.top : side == 0 ? this.bottom : this.side;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        if (world.isRemote) {
            return true;
        }
        else
        {
            TileEntityFoodBowl tileentitydogfoodbowl = (TileEntityFoodBowl)world.getTileEntity(x, y, z);
            player.openGui(DoggyTalentsMod.instance, CommonProxy.GUI_ID_FOOD_BOWL, world, x, y, z);
            return true;
        }
    }
    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: par1World, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	TileEntityFoodBowl foodBowl = (TileEntityFoodBowl) world.getTileEntity(x, y, z);
        List list = null;
        list = world.getEntitiesWithinAABB(EntityDog.class, AxisAlignedBB.getBoundingBox((float)x, (double)(float)y + 0.5D, (float)z, (float)(x + 1), (double)(float)y + 0.5D + 0.05000000074505806D, (float)(z + 1)));

        if (list != null && list.size() > 0)
        {
            for (int l = 0; l < list.size(); l++)
            {
            	EntityDog entitydtdoggy = (EntityDog)list.get(l);
                //TODO entitydtdoggy.saveposition.setBowlX(x);
            	//TODO entitydtdoggy.saveposition.setBowlY(y);
            	//TODO entitydtdoggy.saveposition.setBowlZ(z);
            }
        }
        
        if (entity instanceof EntityItem) {
            EntityItem entityItem = (EntityItem)entity;
            

            if(TileEntityHopper.func_145898_a(foodBowl, entityItem))
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.25F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            
        }

        List list2 = null;
        list2 = world.getEntitiesWithinAABB(EntityDog.class, AxisAlignedBB.getBoundingBox((float)x, (double)(float)y + 0.5D, (float)z, (float)(x + 1), (double)(float)y + 0.5D + 0.05000000074505806D, (float)(z + 1)));

        if (list2 != null && list2.size() > 0)
        {
            TileEntity tileentity1 = world.getTileEntity(x, y, z);

            if (!(tileentity1 instanceof TileEntityFoodBowl))
            {
                return;
            }

            TileEntityFoodBowl tileentitydogfoodbowl1 = (TileEntityFoodBowl)tileentity1;

            for (int j1 = 0; j1 < list2.size(); j1++)
            {
            	EntityDog entitydtdoggy1 = (EntityDog)list2.get(j1);

                if (entitydtdoggy1.getDogHunger() <= 60 && tileentitydogfoodbowl1.getFirstDogFoodStack(entitydtdoggy1) >= 0)
                {
                    tileentitydogfoodbowl1.feedDog(entitydtdoggy1, tileentitydogfoodbowl1.getFirstDogFoodStack(entitydtdoggy1), 1);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFoodBowl();
    }
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (!super.canPlaceBlockAt(world, x, y, z)) {
            return false;
        }
        else {
            return canBlockStay(world, x, y, z);
        }
    }

    @Override
    public void breakBlock(World par1World, int x, int y, int z, Block block, int side) {
        TileEntityFoodBowl tileentitydogfoodbowl = (TileEntityFoodBowl)par1World.getTileEntity(x, y, z);
        tileentitydogfoodbowl.dropContents(par1World, x, y, z);
        super.breakBlock(par1World, x, y, z, block, side);
    }
    
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.top = par1IconRegister.registerIcon("doggytalents:foodTop");
        this.bottom = par1IconRegister.registerIcon("doggytalents:foodBottom");
        this.side = par1IconRegister.registerIcon("doggytalents:foodSide");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!this.canBlockStay(world, x, y, z))
            world.setBlockToAir(x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
    	Block block = world.getBlock(x, y - 1, z);
    	return block.isSideSolid(world, x, y - 1, z, ForgeDirection.UP);
    }
}
