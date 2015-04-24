package doggytalents.entity;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import doggytalents.DoggyTalentsMod;
import doggytalents.ModItems;
import doggytalents.api.DoggyTalentsAPI;
import doggytalents.api.IDogTreat;
import doggytalents.api.IDogTreat.EnumFeedBack;
import doggytalents.api.inferface.ITalent;
import doggytalents.api.registry.TalentRegistry;
import doggytalents.entity.ModeUtil.EnumMode;
import doggytalents.entity.ai.EntityAIDogBeg;
import doggytalents.entity.ai.EntityAIFetchBone;
import doggytalents.entity.ai.EntityAIFollowOwner;
import doggytalents.entity.ai.EntityAIModeAttackTarget;
import doggytalents.entity.ai.EntityAIOwnerHurtByTarget;
import doggytalents.entity.ai.EntityAIOwnerHurtTarget;
import doggytalents.entity.ai.EntityAIShepherdDog;
import doggytalents.helper.LogHelper;
import doggytalents.lib.Constants;
import doggytalents.lib.Reference;
import doggytalents.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

/**
 * @author ProPercivalalb
 */
public class EntityDog extends EntityTameable {
	
    private float timeDogBegging;
    private float prevTimeDogBegging;
    private boolean isShaking;
    public boolean forceShake;
    private float timeDogIsShaking;
    private float prevTimeDogIsShaking;
    private int hungerTick;
   	private int prevHungerTick;
    private int healingTick;
    private int prevHealingTick;
    private int regenerationTick;
    private int prevRegenerationTick;
    private float timeWolfIsHappy;
    private float prevTimeWolfIsHappy;
    private boolean isWolfHappy;
    public boolean hiyaMaster;
    private int reversionTime;
    private boolean hasBone;
    public EntityAIFetchBone aiFetchBone;
    public TalentUtil talents;
    public LevelUtil levels;
    public ModeUtil mode;
    public CoordUtil coords;
    public Map<String, Object> objects;

    public EntityDog(World word) {
        super(word);
        this.objects = new HashMap<String, Object>();
        this.setSize(0.6F, 0.8F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(6, this.aiFetchBone = new EntityAIFetchBone(this, 1.0D, 0.5F, 20.0F));
        this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(9, new EntityAIDogBeg(this, 8.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIModeAttackTarget(this));
        this.targetTasks.addTask(4, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(5, new EntityAITargetNonTamed(this, EntitySheep.class, 200, false));
        this.targetTasks.addTask(6, new EntityAIShepherdDog(this, EntityAnimal.class, 0, false));
        this.setTamed(false);
        TalentHelper.onClassCreation(this);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
        this.updateEntityAttributes();
    }
    
    public void updateEntityAttributes() {
    	if (this.isTamed())
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D + (this.effectiveLevel() + 1.0D));
        else
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }
    
    @Override
    public String getCommandSenderName() {
    	String name = this.getDogName();
    	if(name != "")
    		return name;
    	return super.getCommandSenderName();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.talents = new TalentUtil(this);
        this.levels = new LevelUtil(this);
        this.mode = new ModeUtil(this);
        this.coords = new CoordUtil(this);
        
        this.dataWatcher.addObject(19, new Byte((byte)0)); //Begging
        this.dataWatcher.addObject(20, new Byte((byte)0)); //Dog Texture
        this.dataWatcher.addObject(21, new String("")); //Dog Name
        this.dataWatcher.addObject(22, new String("")); //Talent Data
        this.dataWatcher.addObject(23, new Integer(60)); //Dog Hunger
        this.dataWatcher.addObject(24, new String("0:0")); //Level Data
        this.dataWatcher.addObject(25, new Integer(0)); //Radio Collar
        this.dataWatcher.addObject(26, new Integer(0)); //Obey Others
        this.dataWatcher.addObject(27, new Integer(0)); //Dog Mode
        this.dataWatcher.addObject(28, "-1:-1:-1:-1:-1:-1"); //Dog Mode
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        this.playSound("mob.wolf.step", 0.15F, 1.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setString("version", Reference.MOD_VERSION);
        
        tagCompound.setInteger("doggyTex", this.getTameSkin());
        tagCompound.setString("dogName", this.getDogName());
        tagCompound.setInteger("dogHunger", this.getDogHunger());
        tagCompound.setBoolean("willObey", this.willObeyOthers());
        tagCompound.setBoolean("radioCollar", this.hasRadarCollar());
        
        this.talents.writeTalentsToNBT(tagCompound);
        this.levels.writeTalentsToNBT(tagCompound);
        this.mode.writeToNBT(tagCompound);
        this.coords.writeToNBT(tagCompound);
        TalentHelper.writeToNBT(this, tagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        String lastVersion = tagCompound.getString("version");
        this.setTameSkin(tagCompound.getInteger("doggyTex"));
        this.setDogName(tagCompound.getString("dogName"));
        this.setDogHunger(tagCompound.getInteger("dogHunger"));
        this.setWillObeyOthers(tagCompound.getBoolean("willObey"));
        this.hasRadarCollar(tagCompound.getBoolean("radioCollar"));
        
        this.talents.readTalentsFromNBT(tagCompound);
        this.levels.readTalentsFromNBT(tagCompound);
        this.mode.readFromNBT(tagCompound);
        this.coords.readFromNBT(tagCompound);
        TalentHelper.readFromNBT(this, tagCompound);
    }

    @Override
    public String getLivingSound() {
    	String sound = TalentHelper.getLivingSound(this);
    	if(!"".equals(sound))
    		return sound;
    	
        return this.rand.nextInt(3) == 0 ? (this.isTamed() && this.getHealth() < this.getMaxHealth() / 2 ? "mob.wolf.whine" : "mob.wolf.panting") : "mob.wolf.bark";
    }

    @Override
    public String getHurtSound() {
        return "mob.wolf.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.wolf.death";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemById(-1);
    }
    
    public EntityAISit getSitAI() {
    	return this.aiSit;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(!this.worldObj.isRemote && this.isShaking && !this.forceShake && !this.hasPath() && this.onGround) {
            this.forceShake = true;
            this.timeDogIsShaking = 0.0F;
            this.prevTimeDogIsShaking = 0.0F;
            this.worldObj.setEntityState(this, (byte)8);
        }
        
        if(Constants.IS_HUNGER_ON) {
        	this.prevHungerTick = this.hungerTick;
        	
	        if (this.riddenByEntity == null && !this.isSitting() /** && !this.mode.isMode(EnumMode.WANDERING) && !this.level.isDireDog() || worldObj.getWorldInfo().getWorldTime() % 2L == 0L **/)
	        	this.hungerTick += 1;
	        
	        this.hungerTick += TalentHelper.onHungerTick(this, this.hungerTick - this.prevHungerTick);
	        
	        if (this.hungerTick > 400) {
	            this.setDogHunger(this.getDogHunger() - 1);
	            this.hungerTick -= 400;
	        }
        }
        
        if(Constants.DOGS_IMMORTAL) {
        	this.prevRegenerationTick = this.regenerationTick;
        	
	        if(this.getHealth() <= 1) {
	        	this.regenerationTick += 1;
	        	
	        	this.regenerationTick += TalentHelper.onRegenerationTick(this, this.regenerationTick - this.prevRegenerationTick);
	        }
	        
	        if(this.regenerationTick >= 12000) {
	            this.setHealth(this.getHealth() + 1);
	            this.worldObj.setEntityState(this, (byte)7);
	            this.regenerationTick = 0;
	        }
    	}
        
        if(this.getHealth() != 1) {
	        this.prevHealingTick = this.healingTick;
	        this.healingTick += this.nourishment();
	        
	        if (this.healingTick >= 6000) {
	            if (this.getHealth() < this.getMaxHealth())
	            	this.setHealth(this.getHealth() + 1);
	            
	            this.healingTick = 0;
	        }
        }
        
        if (this.getHealth() <= 0 && this.isImmortal()) {
            this.deathTime = 0;
            this.setHealth(1);
        }
        
        if(this.getDogHunger() == 0 && this.worldObj.getWorldInfo().getWorldTime() % 100L == 0L && this.getHealth() > 1) {
            this.attackEntityFrom(DamageSource.generic, 1);
            this.fleeingTick = 0;
        }
        
        if (this.levels.isDireDog() && Constants.DIRE_PARTICLES) {
            for (int i = 0; i < 2; i++) {
                worldObj.spawnParticle("portal", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.posY + rand.nextDouble() * (double)height) - 0.25D, posZ + (rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2D);
            }
        }
        
        if(this.reversionTime > 0)
        	this.reversionTime -= 1;
        
        TalentHelper.onLivingUpdate(this);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.prevTimeDogBegging = this.timeDogBegging;

        if(this.isBegging())
            this.timeDogBegging += (1.0F - this.timeDogBegging) * 0.4F;
        else
            this.timeDogBegging += (0.0F - this.timeDogBegging) * 0.4F;

        if(this.isBegging())
            this.numTicksToChaseTarget = 10;

        if(this.isWet()) {
            this.isShaking = true;
            this.forceShake = false;
            this.timeDogIsShaking = 0.0F;
            this.prevTimeDogIsShaking = 0.0F;
        }
        else if((this.isShaking || this.forceShake) && this.forceShake) {
        	
            if(this.timeDogIsShaking == 0.0F)
                this.playSound("mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);

            this.prevTimeDogIsShaking = this.timeDogIsShaking;
            this.timeDogIsShaking += 0.05F;

            if(this.prevTimeDogIsShaking >= 2.0F) {
            	if(this.rand.nextInt(15) < this.talents.getLevel("fisherdog") * 2) {
                    if(this.rand.nextInt(15) < this.talents.getLevel("hellhound") * 2) {
                    	if(!this.worldObj.isRemote) {
                    		dropItem(Items.cooked_fished, 1);
                    	}
                    }
                    else {
                    	if(!this.worldObj.isRemote) {
                    		dropItem(Items.fish, 1);
                    	}
                    }
                }
            	
                this.isShaking = false;
                this.forceShake = false;
                this.prevTimeDogIsShaking = 0.0F;
                this.timeDogIsShaking = 0.0F;
            }

            if(this.timeDogIsShaking > 0.4F) {
                float f = (float)this.boundingBox.minY;
                int i = (int)(MathHelper.sin((this.timeDogIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

                for (int j = 0; j < i; ++j)
                {
                    float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    this.worldObj.spawnParticle("splash", this.posX + (double)f1, (double)(f + 0.8F), this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ);
                }
            }
        }
        
        if(this.rand.nextInt(200) == 0) {
        	this.hiyaMaster = true;
        }
        
        if (((this.isBegging()) || (this.hiyaMaster)) && (!this.isWolfHappy))
        {
        	this.isWolfHappy = true;
          	this.timeWolfIsHappy = 0.0F;
          	this.prevTimeWolfIsHappy = 0.0F;
        }
        else  {
        	hiyaMaster = false;
        }
        if (this.isWolfHappy)
        {
        	if (this.timeWolfIsHappy % 1.0F == 0.0F)
        	{
        		this.worldObj.playSoundAtEntity(this, "mob.wolf.panting", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        	}
        	this.prevTimeWolfIsHappy = this.timeWolfIsHappy;
        	this.timeWolfIsHappy += 0.05F;
        	if (this.prevTimeWolfIsHappy >= 8.0F)
        	{
        		this.isWolfHappy = false;
        		this.prevTimeWolfIsHappy = 0.0F;
        		this.timeWolfIsHappy = 0.0F;
        	}
        }
        
        if(this.isTamed()) {
    		EntityPlayer player = (EntityPlayer)this.getOwner();
    		
    		if(player != null) {
    			float distanceToOwner = player.getDistanceToEntity(this);

                if (distanceToOwner <= 2F && this.hasBone()) {
                	if(!this.worldObj.isRemote) {
                		this.entityDropItem(new ItemStack(ModItems.throwBone, 1, 1), 0.0F);
                	}
                	
                    this.setHasBone(false);
                }
    		}
    	}
        
        TalentHelper.onUpdate(this);
    }
    
    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        if (this.riddenByEntity instanceof EntityPlayer) {
            this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
            this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafe = ((EntityPlayer)this.riddenByEntity).moveStrafing * 0.5F;
            forward = ((EntityPlayer)this.riddenByEntity).moveForward;

            if (forward <= 0.0F)
                forward *= 0.25F;

            if (this.onGround) {
                if (forward > 0.0F) {
                    float f2 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
                    float f3 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
                    this.motionX += (double)(-0.4F * f2 * 0.15F); // May change
                    this.motionZ += (double)(0.4F * f3 * 0.15F);
                }
            }

            this.stepHeight = 1.0F;
            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.2F;

            if (!this.worldObj.isRemote)  {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() / 4);
                super.moveEntityWithHeading(strafe, forward);
            }

            if (this.onGround) {
                //this.jumpPower = 0.0F;
               // this.setHorseJumping(false);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d0 = this.posX - this.prevPosX;
            double d1 = this.posZ - this.prevPosZ;
            float f4 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F)
                f4 = 1.0F;

            this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else {
            this.stepHeight = 0.5F;
            this.jumpMovementFactor = 0.02F;
            super.moveEntityWithHeading(strafe, forward);
        }
    }
    
    @Override
    public float getAIMoveSpeed() {
    	double speed = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    	
    	speed += TalentHelper.addToMoveSpeed(this);
    	
    	if((!(this.getAttackTarget() instanceof EntityDog) && !(this.getAttackTarget() instanceof EntityPlayer)) || this.riddenByEntity instanceof EntityPlayer)
    		if (this.levels.isDireDog())
    			speed += 0.05D;
    	
    	if(this.riddenByEntity instanceof EntityPlayer)
    		speed /= 4;
    	
        return (float)speed;
    }

    @SideOnly(Side.CLIENT)
    public boolean getDogShaking() {
        return this.isShaking;
    }

    @SideOnly(Side.CLIENT)
    public float getShadingWhileShaking(float partialTickTime) {
        return 0.75F + (this.prevTimeDogIsShaking + (this.timeDogIsShaking - this.prevTimeDogIsShaking) * partialTickTime) / 2.0F * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float partialTickTime, float startTime) {
        float f2 = (this.prevTimeDogIsShaking + (this.timeDogIsShaking - this.prevTimeDogIsShaking) * partialTickTime + startTime) / 1.8F;

        if (f2 < 0.0F)
            f2 = 0.0F;
        else if (f2 > 1.0F)
            f2 = 1.0F;

        return MathHelper.sin(f2 * (float)Math.PI) * MathHelper.sin(f2 * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }
    
    public boolean isImmortal() {
        return this.isTamed() && Constants.DOGS_IMMORTAL || this.levels.isDireDog();
    }
    
    @Override
    public float getEyeHeight() {
        return this.height * 0.8F;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float partialTickTime) {
        return (this.prevTimeDogBegging + (this.timeDogBegging - this.prevTimeDogBegging) * partialTickTime) * 0.15F * (float)Math.PI;
    }

    @Override
    public int getVerticalFaceSpeed() {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }
    
    @Override
    protected void fall(float distance) {
    	distance = ForgeHooks.onLivingFall(this, distance);
        if (distance <= 0) return;
        PotionEffect potioneffect = this.getActivePotionEffect(Potion.jump);
        float f1 = potioneffect != null ? (float)(potioneffect.getAmplifier() + 1) : 0.0F;
        int i = MathHelper.ceiling_float_int(distance - 3.0F - f1) - TalentHelper.fallProtection(this);

        if (i > 0 && !TalentHelper.isImmuneToFalls(this)) {
            this.playSound(this.func_146067_o(i), 1.0F, 1.0F);
            this.attackEntityFrom(DamageSource.fall, (float)i);
            int j = MathHelper.floor_double(this.posX);
            int k = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            int l = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlock(j, k, l);

            if (block.getMaterial() != Material.air) {
                Block.SoundType soundtype = block.stepSound;
                this.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
            }
        }
    }


    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if (this.isEntityInvulnerable())
            return false;
        else {
        	if(!TalentHelper.attackEntityFrom(this, damageSource, damage))
        		return false;
        	
            Entity entity = damageSource.getEntity();
            this.aiSit.setSitting(false);

            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
                damage = (damage + 1.0F) / 2.0F;

            return super.attackEntityFrom(damageSource, damage);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
    	if(!TalentHelper.shouldDamageMob(this, entity))
    		return false;
    	
    	int damage = 4 + (this.effectiveLevel() + 1) / 2;
        damage = TalentHelper.attackEntityAsMob(this, entity, damage);
        
        if (entity instanceof EntityZombie)
            ((EntityZombie)entity).setAttackTarget(this);
        
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)damage);
    }

    @Override
    public void setTamed(boolean p_70903_1_) {
        super.setTamed(p_70903_1_);

        if (p_70903_1_)
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        else
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack stack = player.inventory.getCurrentItem();

        if(TalentHelper.interactWithPlayer(this, player))
        	return true;
        
        if (this.isTamed()) {
            if (stack != null) {
            	int foodValue = this.foodValue(stack);
            	
            	if(foodValue != 0 && this.getDogHunger() < 120 && this.canInteract(player)) {
            		 if(!player.capabilities.isCreativeMode && --stack.stackSize <= 0)
                         player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
            		
                    this.setDogHunger(this.getDogHunger() + foodValue);
                    return true;
                }
            	else if(stack.getItem() == Items.bone && this.canInteract(player)) {
            		if (!this.worldObj.isRemote) {
                        if(this.ridingEntity != null)
                        	this.mountEntity(null);
                        else
                         	this.mountEntity(player);
                    }
                    return true;
                }
            	else if(stack.getItem() == Items.stick && this.canInteract(player)) {
            		player.openGui(DoggyTalentsMod.instance, CommonProxy.GUI_ID_DOGGY, this.worldObj, this.getEntityId(), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
                 	return true;
                }
                else if(stack.getItem() == ModItems.radioCollar && this.canInteract(player) && !this.hasRadarCollar()) {
                	if(!player.capabilities.isCreativeMode && --stack.stackSize <= 0)
                         player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                 	this.hasRadarCollar(true);
                 	return true;
                }
                else if(stack.getItem() instanceof IDogTreat && this.canInteract(player)) {
                 	IDogTreat treat = (IDogTreat)stack.getItem();
                 	EnumFeedBack type = treat.canGiveToDog(player, this, this.levels.getLevel(), this.levels.getDireLevel());
                 	treat.giveTreat(type, player, this);
                 	return true;
                }
                else if(stack.getItem() == ModItems.collarShears && this.func_152114_e(player)) {
                	if(!this.worldObj.isRemote) {
                		this.setTamed(false);
                        this.setPathToEntity(null);
                        this.setSitting(false);
                        this.setHealth(8);
                        this.talents.resetTalents();
                        this.func_152115_b("");
                        this.setWillObeyOthers(false);
                        this.mode.setMode(EnumMode.DOCILE);
                        if(this.hasRadarCollar())
                        	this.dropItem(ModItems.radioCollar, 1);
                        this.hasRadarCollar(false);
                        this.reversionTime = 40;
                     }

                	return true;
                }
                else if(stack.getItem() == Items.cake && this.canInteract(player) && this.getHealth() == 1) {
                	if(!player.capabilities.isCreativeMode && --stack.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                 	
                    if(!this.worldObj.isRemote) {
                        this.aiSit.setSitting(true);
                        this.setHealth(this.getMaxHealth());
                        this.setDogHunger(120);
                        this.regenerationTick = 0;
                        this.setPathToEntity((PathEntity)null);
                        this.setTarget((Entity)null);
                        this.setAttackTarget((EntityLivingBase)null);
                        this.playTameEffect(true);
                        this.worldObj.setEntityState(this, (byte)7);
                    }

                    return true;
                }
                else if(stack.getItem() == Items.stick) {
                	player.openGui(DoggyTalentsMod.instance, CommonProxy.GUI_ID_DOGGY, this.worldObj, this.getEntityId(), 0, 0);
                	return true;
                }
            }

            if (this.func_152114_e(player) && !this.worldObj.isRemote && !this.isBreedingItem(stack)) {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity((PathEntity)null);
                this.setTarget((Entity)null);
                this.setAttackTarget((EntityLivingBase)null);
            }
        }
        else if(stack.getItem() == ModItems.collarShears && this.reversionTime < 1 && !worldObj.isRemote) {
            this.setDead();
            EntityWolf wolf = new EntityWolf(this.worldObj);
            wolf.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.worldObj.spawnEntityInWorld(wolf);
            return true;
        }
        else if(stack != null && stack.getItem() == Items.bone) {
        	if(!player.capabilities.isCreativeMode && --stack.stackSize <= 0)
                player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);

            if(!this.worldObj.isRemote) {
                if(this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.setPathToEntity((PathEntity)null);
                    this.setAttackTarget((EntityLivingBase)null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.func_152115_b(player.getUniqueID().toString());
                    this.playTameEffect(true);
                    this.worldObj.setEntityState(this, (byte)7);
                }
                else {
                    this.playTameEffect(false);
                    this.worldObj.setEntityState(this, (byte)6);
                }
            }

            return true;
        }

        return super.interact(player);
    }
    
    @Override
    protected boolean isMovementBlocked() {
        return this.isPlayerSleeping() || this.riddenByEntity instanceof EntityPlayer || super.isMovementBlocked();
    }

    @Override
    public double getYOffset() {
        return (double)-1.0F;
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if (this.getHealth() <= 1)
            return false;

        if(!TalentHelper.isPostionApplicable(this, potionEffect))
        	return false;

        return true;
    }
    
    @Override
    public void setFire(int amount) {
    	if(TalentHelper.setFire(this, amount))
    		super.setFire(amount);
    }
    
    public int foodValue(ItemStack stack) {
    	if(stack == null || stack.getItem() == null)
    		return 0;
    	
    	int foodValue = 0;
    	
    	Item item = stack.getItem();
    	
        if(stack.getItem() != Items.rotten_flesh && item instanceof ItemFood) {
            ItemFood itemfood = (ItemFood)item;

            if (itemfood.isWolfsFavoriteMeat())
            	foodValue = 40;
        }
        
        TalentHelper.changeFoodValue(this, stack, foodValue);

        return foodValue;
    }
    
    public int masterOrder() {
    	int order = 0;
        EntityPlayer player = (EntityPlayer)this.getOwner();

        if (player != null) {
        	
            float distanceAway = player.getDistanceToEntity(this);
            ItemStack itemstack = player.inventory.getCurrentItem();

            if (itemstack != null && (itemstack.getItem() instanceof ItemTool) && distanceAway <= 20F)
                order = 1;

            if (itemstack != null && ((itemstack.getItem() instanceof ItemSword) || (itemstack.getItem() instanceof ItemBow)))
                order = 2;

            if (itemstack != null && itemstack.getItem() == Items.wheat)
                order = 3;
        }

        return order;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 8) {
            this.forceShake = true;
            this.timeDogIsShaking = 0.0F;
            this.prevTimeDogIsShaking = 0.0F;
        }
        else
            super.handleHealthUpdate(p_70103_1_);
    }
    
    public float getWagAngle(float f, float f1) {
        float f2 = (this.prevTimeWolfIsHappy + (this.timeWolfIsHappy - this.prevTimeWolfIsHappy) * f + f1) / 2.0F;
        if (f2 < 0.0F)
        	f2 = 0.0F;
        else if (f2 > 2.0F)
        	f2 %= 2.0F;
        return MathHelper.sin(f2 * (float)Math.PI * 11.0F) * 0.3F * (float)Math.PI;
      }

    @SideOnly(Side.CLIENT)
    public float getTailRotation() {
        return this.isTamed() ? (0.55F - ((this.getMaxHealth() - this.getHealth()) / (this.getMaxHealth() / 20.0F)) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack != null && DoggyTalentsAPI.BREED_WHITELIST.containsItem(stack);
    }

    @Override
    public boolean isPlayerSleeping() {
        return false;
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return TalentHelper.canBreatheUnderwater(this);
    }
    
    public boolean canInteract(EntityPlayer player) {
    	return this.func_152114_e(player) || this.willObeyOthers();
    }
    
    public int nourishment() {
        int amount = 0;

        if (this.getDogHunger() > 0) {
            amount = 40 + 4 * (this.effectiveLevel() + 1);

            if (isSitting() && this.talents.getLevel("quickhealer") == 5) {
                amount += 20 + 2 * (this.effectiveLevel() + 1);
            }

            if (!this.isSitting()) {
                amount *= 5 + this.talents.getLevel("quickhealer");
                amount /= 10;
            }
        }

        return amount;
    }
    
    @Override
    public void playTameEffect(boolean successful) {
       super.playTameEffect(successful);
    }
    
    public int effectiveLevel() {
        return (this.levels.getLevel() + this.levels.getDireLevel()) / 10;
    }

    public int getTameSkin() {
        return this.dataWatcher.getWatchableObjectByte(20);
    }

    public void setTameSkin(int index) {
        this.dataWatcher.updateObject(20, Byte.valueOf((byte)index));
    }
    
    public String getDogName() {
        return this.dataWatcher.getWatchableObjectString(21);
    }
    
    public void setDogName(String var1) {
       this.dataWatcher.updateObject(21, var1);
    }
    
    public void setWillObeyOthers(boolean flag) {
    	this.dataWatcher.updateObject(26, flag ? 1 : 0);
    }
    
    public boolean willObeyOthers() {
    	return this.dataWatcher.getWatchableObjectInt(26) != 0;
    }
    
    public int points() {
        return this.levels.getLevel() + this.levels.getDireLevel() + (this.levels.isDireDog() ? 15 : 0) + (this.getGrowingAge() < 0 ? 0 : 15);
    }

    public int spendablePoints() {
        return this.points() - this.usedPoints();
    }
    
    public int usedPoints() {
		return TalentHelper.getUsedPoints(this);
    }
    
    public int deductive(int par1) {
        byte byte0 = 0;
        switch(par1) {
        case 1: return 1;
		case 2: return 3;
        case 3: return 6;
        case 4: return 10;
        case 5: return 15;
        default: return 0;
        }
    }
    
    @Override
    public EntityDog createChild(EntityAgeable entityAgeable) {
        EntityDog entitydog = new EntityDog(this.worldObj);
        String uuid = this.func_152113_b();

        if (uuid != null && uuid.trim().length() > 0) {
            entitydog.func_152115_b(uuid);
            entitydog.setTamed(true);
        }
        
        entitydog.setGrowingAge(-24000 * (Constants.TEN_DAY_PUPS ? 10 : 1));

        return entitydog;
    }

    public void setBegging(boolean flag) {
    	this.dataWatcher.updateObject(19, Byte.valueOf((byte)(flag ? 1 : 0)));
    }

    public boolean isBegging() {
        return this.dataWatcher.getWatchableObjectByte(19) == 1;
    }
    
    public int getDogHunger() {
		return this.dataWatcher.getWatchableObjectInt(23);
	}
    
    public void setDogHunger(int par1) {
    	this.dataWatcher.updateObject(23, MathHelper.clamp_int(par1, 0, 120));
    }
    
    public void hasRadarCollar(boolean flag) {
    	this.dataWatcher.updateObject(25, flag ? 1 : 0);
    }
    
    public boolean hasRadarCollar() {
    	return this.dataWatcher.getWatchableObjectInt(25) != 0;
    }
    
    public void setHasBone(boolean hasBone) {
    	this.hasBone = hasBone;
    }
    
    public boolean hasBone() {
    	return this.hasBone;
    }
    
    @Override
    public boolean canMateWith(EntityAnimal entityAnimal) {
        if (entityAnimal == this)
            return false;
        else if (!this.isTamed())
            return false;
        else if (!(entityAnimal instanceof EntityDog))
            return false;
        else {
            EntityDog entityDog = (EntityDog)entityAnimal;
            return !entityDog.isTamed() ? false : (entityDog.isSitting() ? false : this.isInLove() && entityDog.isInLove());
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean func_142018_a(EntityLivingBase entityToAttack, EntityLivingBase owner) {
    	if(TalentHelper.canAttackEntity(this, entityToAttack))
    		return true;
    	
        if (!(entityToAttack instanceof EntityCreeper) && !(entityToAttack instanceof EntityGhast)) {
            if (entityToAttack instanceof EntityDog) {
                EntityDog entityDog = (EntityDog)entityToAttack;

                if (entityDog.isTamed() && entityDog.getOwner() == owner)
                    return false;
            }

            return entityToAttack instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer)owner).canAttackPlayer((EntityPlayer)entityToAttack) ? false : !(entityToAttack instanceof EntityHorse) || !((EntityHorse)entityToAttack).isTame();
        }
        else {
            return false;
        }
    }
    
    @Override
    public boolean canAttackClass(Class p_70686_1_) {
    	if(TalentHelper.canAttackClass(this, p_70686_1_))
    		return true;
    	
        return super.canAttackClass(p_70686_1_);
    }
}