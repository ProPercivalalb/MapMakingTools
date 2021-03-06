package mapmakingtools.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author ProPercivalalb
 */
public class GuiButtonSmall extends GuiButton {
    
	public GuiButtonSmall(int id, int xPosition, int yPosition, int width, int height, String text) {
        super(id, xPosition, yPosition, width, height, text);
    }
    
    @Override
    public void drawButton(Minecraft mc, int i, int j, float partialTicks) {
        if(this.visible) {
        	FontRenderer fontRenderer = mc.fontRenderer;
        	mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = i >= this.x && j >= y && i < this.x + this.width && j < y + this.height;
            int hoverState = getHoverState(this.hovered);
            this.drawTexturedModalRect(this.x, y, 0, 46 + hoverState * 20, this.width / 2, this.height / 2);//top left
            this.drawTexturedModalRect(this.x + this.width / 2, y, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height / 2);//top right
            this.drawTexturedModalRect(this.x, y + this.height / 2, 0, 46 + hoverState * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom left
            this.drawTexturedModalRect(this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + hoverState * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom right
            this.mouseDragged(mc, i, j);
             
            if(!this.enabled) {
                this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, y + (this.height - 8) / 2, 0xffa0a0a0);
            } 
            else {
            	if(this.hovered) {
            		this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, y + (this.height - 8) / 2, 0xffffa0);
            	}
            	else {
            		this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, y + (this.height - 8) / 2, 0xe0e0e0);
            	}
        	}
    	}
	}
}
