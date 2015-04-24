package doggytalents.entity.ai;

import doggytalents.entity.EntityDog;
import doggytalents.entity.ModeUtil.EnumMode;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author ProPercivalalb
 */
public class EntityAIShepherdDog extends EntityAINearestAttackableTarget {

	public EntityDog dog;
	
	public EntityAIShepherdDog(EntityDog dog, Class target, int targetChance, boolean shouldCheckSight) {
		super(dog, target, targetChance, shouldCheckSight);
		this.dog = dog;
	}

	@Override
	public boolean shouldExecute() {
		int order = dog.masterOrder();
        return order == 3 && this.dog.mode.isMode(EnumMode.DOCILE) && this.dog.isTamed() && this.dog.riddenByEntity == null && this.dog.talents.getLevel("shepherddog") > 0 && super.shouldExecute();
    }
	
	@Override
	protected double getTargetDistance() {
	    return 24.0D;
	}
	
	@Override
	public boolean continueExecuting() {
		int order = dog.masterOrder();
        return order == 3 && super.continueExecuting();
	}
}
