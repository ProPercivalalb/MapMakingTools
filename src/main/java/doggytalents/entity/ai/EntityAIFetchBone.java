package doggytalents.entity.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import doggytalents.ModItems;
import doggytalents.entity.EntityDog;
import doggytalents.entity.ModeUtil.EnumMode;

/**
 * @author ProPercivalalb
 */
public class EntityAIFetchBone extends EntityAIBase {
    private EntityDog theDog;
    private EntityLivingBase theOwner;
    private EntityItem theBone;
    private World theWorld;
    private double moveSpeed;
    private PathNavigate petPathfinder;
    private int tenTickTimer;
    private float maxDist;
    private float minDist;
    private boolean preShouldAvoidWater;

    public EntityAIFetchBone(EntityDog par1EntityDog, double moveSpeed, float minDistance, float maxDistance) {
        this.theDog = par1EntityDog;
        this.theWorld = par1EntityDog.worldObj;
        this.moveSpeed = moveSpeed;
        this.petPathfinder = par1EntityDog.getNavigator();
        this.minDist = minDistance;
        this.maxDist = maxDistance;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
    	this.theBone = this.getClosestsBone();
    	EntityLivingBase possibleOwner = this.theDog.getOwner();
    	
        if(this.theBone == null) {
            return false;
        }
        else if(possibleOwner == null) {
        	this.theBone = null;
            return false;
        }
        else if(this.theDog.isSitting()) {
        	this.theBone = null;
            return false;
        }
        else if(!this.theDog.mode.isMode(EnumMode.DOCILE)) {
        	this.theBone = null;
            return false;
        }
        else if(this.theDog.riddenByEntity instanceof EntityPlayer) {
        	this.theBone = null;
            return false;
        }
        else if(!this.theDog.isTamed()) {
        	this.theBone = null;
            return false;
        }
        else if(this.theDog.hasBone()) {
        	this.theBone = null;
            return false;
        }
        else if(this.theDog.getHealth() <= 1) {
        	this.theBone = null;
            return false;
        }
        else if(this.theDog.getDistanceSqToEntity(this.theBone) > (double)(this.maxDist * this.maxDist) || this.theDog.getDistanceSqToEntity(this.theBone) < (double)(this.minDist * this.minDist)) {
        	this.theBone = null;
            return false;
        }
        else {
        	this.theOwner = possibleOwner;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return !this.petPathfinder.noPath() && this.theOwner != null && !this.theDog.hasBone() && this.theBone != null && !this.theBone.isDead && !(this.theDog.getDistanceSqToEntity(this.theBone) > (double)(this.maxDist * this.maxDist) || this.theDog.getDistanceSqToEntity(this.theBone) < (double)(this.minDist * this.minDist)) && !this.theDog.isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        this.tenTickTimer = 0;
        this.preShouldAvoidWater = this.theDog.getNavigator().getAvoidsWater();
        this.theDog.getNavigator().setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        this.theDog.getNavigator().setAvoidsWater(this.preShouldAvoidWater);
    }
    
    public EntityItem getClosestsBone() {
        EntityItem entityItem = null;
        
        if(this.theDog.hasBone())
        	return null;
        
        List list = this.theWorld.getEntitiesWithinAABBExcludingEntity(this.theDog, this.theDog.boundingBox.expand((double)maxDist, (double)maxDist, (double)maxDist));
        for (int i = 0; i < list.size(); i++) {
            Entity listEntity = (Entity)list.get(i);

            if ((listEntity instanceof EntityItem) && ((EntityItem)listEntity).getEntityItem().getItem() == ModItems.throwBone && ((EntityItem)listEntity).getEntityItem().getItemDamage() == 0) {
                entityItem = (EntityItem)listEntity;
            }
        }
        return entityItem;
    }

    @Override
    public void updateTask() {
        this.theDog.getLookHelper().setLookPositionWithEntity(this.theBone, 10.0F, (float)this.theDog.getVerticalFaceSpeed());

        if(!this.theDog.isSitting()) {
            if (--this.tenTickTimer <= 0) {
                this.tenTickTimer = 10;

                this.petPathfinder.tryMoveToEntityLiving(this.theBone, this.moveSpeed);
            }
        }
        
        if(this.theDog.getDistanceSqToEntity(this.theBone) < (double)(1.5F * 1.5F) && !this.theDog.hasBone()) {
        	if(this.theBone.isEntityAlive()) {
        		this.theBone.attackEntityFrom(DamageSource.generic, 12F);
        		this.theDog.setHasBone(true);
        		this.theBone = null;
                this.theDog.setPathToEntity((PathEntity)null);
                this.theDog.setTarget((Entity)null);
                this.theDog.setAttackTarget((EntityLivingBase)null);
        	}
        }
    }
    
    public EntityItem getCurrentTarget() {
    	return this.theBone;
    }
}
