package doggytalents.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import doggytalents.entity.EntityDog;
import doggytalents.helper.LogHelper;
import doggytalents.lib.Constants;
import doggytalents.lib.ResourceReference;

/**
 * @author ProPercivalalb
 */
@SideOnly(Side.CLIENT)
public class RenderDog extends RenderLiving {
	
    public RenderDog(ModelBase p_i1269_1_, ModelBase p_i1269_2_, float shadowSize) {
        super(p_i1269_1_, shadowSize);
        this.setRenderPassModel(p_i1269_2_);
    }

    protected float handleRotationFloat(EntityDog dog, float partialTickTime) {
        return dog.getTailRotation();
    }
    
    @Override
    protected void renderLivingAt(EntityLivingBase p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_)
    {
        if (p_77039_1_.isEntityAlive() && p_77039_1_.isPlayerSleeping())
        {
            super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_ + 0.5F, p_77039_6_);
        }
        else
        {
            super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
        }
    }

    @Override
    protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
    {
        if (p_77043_1_.isEntityAlive() && p_77043_1_.isPlayerSleeping())
        {
            //GL11.glRotatef(p_77043_1_.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        }
        else
        {
            super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
        }
    }

    protected int shouldRenderPass(EntityDog dog, int renderPass, float partialTickTime) {
        float brightness = dog.getBrightness(partialTickTime) * dog.getShadingWhileShaking(partialTickTime);
    	
        if (renderPass == 0 && dog.getDogShaking()) {
            this.bindTexture(this.getEntityTexture(dog));
            GL11.glColor3f(brightness, brightness, brightness);
            return 1;
        }
        else if(renderPass == 1 && (dog.getHealth() == 1 && dog.isImmortal() && Constants.RENDER_BLOOD)) {
        	this.bindTexture(ResourceReference.doggyHurt);
            GL11.glColor3f(brightness, brightness, brightness);
            return 1;
        }
        else if(renderPass == 2 && dog.hasRadarCollar()) {
        	this.bindTexture(ResourceReference.doggyRadioCollar);
            GL11.glColor3f(brightness, brightness, brightness);
            return 1;
        }
        else
            return -1;
    }

    protected ResourceLocation getEntityTexture(EntityDog dog) {
        if(dog.isTamed())
			return ResourceReference.getTameSkin(dog.getTameSkin());
    	
        return ResourceReference.doggyWild;
    }
    
    @Override
    protected void passSpecialRender(EntityLivingBase entityLivingBase, double p_77033_2_, double p_77033_4_, double p_77033_6_) {
    	EntityDog dog = (EntityDog)entityLivingBase;
        
        if(!dog.getDogName().isEmpty())
        	super.passSpecialRender(entityLivingBase, p_77033_2_, p_77033_4_, p_77033_6_);
    }
    
    @Override
    protected void func_96449_a(EntityLivingBase entityLivingBase, double x, double y, double z, String displayName, float scale, double distanceFromPlayer) {
    	super.func_96449_a(entityLivingBase, x, y, z, displayName, scale, distanceFromPlayer);
        
    	EntityDog dog = (EntityDog)entityLivingBase;
    	
    	if (distanceFromPlayer < 100.0D) {
        	
            y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.016666668F * 0.7F);
        	
            String tip = dog.mode.getMode().getTip();
            
            if(dog.isImmortal() && dog.getHealth() <= 1)
            	tip = "(I)";
            
            String label = String.format("%s(%d)", tip, dog.getDogHunger());
            
            if (entityLivingBase.isPlayerSleeping())
                this.renderLivingLabel(entityLivingBase, label,  x, y - 0.5D, z, 64, 0.7F);
            else
                this.renderLivingLabel(entityLivingBase, label, x, y, z, 64, 0.7F);
        }
    	
    	if (distanceFromPlayer < 100.0D) {
    		y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.016666668F * 0.5F);
              
           if(this.renderManager.livingPlayer.isSneaking())
        	   this.renderLivingLabel(entityLivingBase, dog.getOwner().getCommandSenderName(), x, y, z, 5, 0.5F);
    	}
    }
    
    protected void renderLivingLabel(Entity p_147906_1_, String p_147906_2_, double p_147906_3_, double p_147906_5_, double p_147906_7_, int p_147906_9_, float scale) {
        double d3 = p_147906_1_.getDistanceSqToEntity(this.renderManager.livingPlayer);

        if (d3 <= (double)(p_147906_9_ * p_147906_9_)) {
            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            float f1 = 0.016666668F * scale;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)p_147906_3_ + 0.0F, (float)p_147906_5_ + p_147906_1_.height + 0.5F, (float)p_147906_7_);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-f1, -f1, f1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.instance;
            byte b0 = 0;

            if (p_147906_2_.equals("deadmau5"))
                b0 = -10;

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            int j = fontrenderer.getStringWidth(p_147906_2_) / 2;
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
            tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
            tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, 553648127);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, -1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entityLivingBase, int renderPass, float partialTickTime) {
        return this.shouldRenderPass((EntityDog)entityLivingBase, renderPass, partialTickTime);
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entityLivingBase, float partialTickTime) {
        return this.handleRotationFloat((EntityDog)entityLivingBase, partialTickTime);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityDog)entity);
    }
}