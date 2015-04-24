package doggytalents.entity;

import java.util.List;

import doggytalents.entity.ModeUtil.EnumMode;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class EntityDoggyBeam extends EntityThrowable {
	
    public EntityDoggyBeam(World par1World) {
        super(par1World);
    }

    public EntityDoggyBeam(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityDoggyBeam(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    @Override
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
        if (par1MovingObjectPosition.entityHit != null && par1MovingObjectPosition.entityHit instanceof EntityLiving)
        {
            byte var2 = 0;
            
            List nearEnts = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(100D, 10D, 100D));
            for (Object o : nearEnts)
            {
                if (o instanceof EntityDog)
                {
                	EntityDog dog = (EntityDog)o;
                	if(!dog.isSitting() && par1MovingObjectPosition.entityHit != dog && dog.func_142018_a((EntityLiving)par1MovingObjectPosition.entityHit, dog.getOwner()) && this.getThrower() instanceof EntityPlayer && dog.canInteract((EntityPlayer)this.getThrower())) {
                		if(dog.getDistanceToEntity(par1MovingObjectPosition.entityHit) < this.getTargetDistance(dog) && (dog.mode.isMode(EnumMode.AGGRESIVE) || dog.mode.isMode(EnumMode.TACTICAL))) {
                			dog.setAttackTarget((EntityLiving)par1MovingObjectPosition.entityHit);
                		}
                	}
                }
            }
        }

        for (int var3 = 0; var3 < 8; ++var3)
        {
            this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!this.worldObj.isRemote)
        {
            this.setDead();
        }
    }
    
    protected double getTargetDistance(EntityDog dog) {
        IAttributeInstance iattributeinstance = dog.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }
}
