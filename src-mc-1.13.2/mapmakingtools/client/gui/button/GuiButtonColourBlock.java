package mapmakingtools.client.gui.button;

import mapmakingtools.lib.ResourceLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author ProPercivalalb
 */
public class GuiButtonColourBlock extends GuiButton {

	public int textColourIndex = TextColour.GOLD.ordinal();
	private Minecraft mc = Minecraft.getInstance();
	
	public GuiButtonColourBlock(int id, int xPosition, int yPosition, int width, int height) {
		super(id, xPosition, yPosition, width, height, "");
	}

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
        	TextColour colour = getCurrentColour(textColourIndex);
        	FontRenderer fontrenderer = mc.fontRenderer;
        	mc.getTextureManager().bindTexture(ResourceLib.BUTTON_TEXT_COLOUR);
            GlStateManager.color4f(colour.red / 255F, colour.green / 255F, colour.blue / 255F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.hovered);
            if(this.width < 20 && this.height < 20) {
            	this.drawTexturedModalRect(x, y, 0, 46 + hoverState * 20, width / 2, height / 2);//top left
                this.drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 46 + hoverState * 20, width / 2, height / 2);//top right
                this.drawTexturedModalRect(x, y + height / 2, 0, 46 + hoverState * 20 + 20 - height / 2, width / 2, height / 2);//bottom left
                this.drawTexturedModalRect(x + width / 2, y + height / 2, 200 - width / 2, 46 + hoverState * 20 + 20 - height / 2, width / 2, height / 2);//bottom right
            }
            else {
	            this.drawTexturedModalRect(this.x, this.y, 0, 46 + hoverState * 20, this.width / 2, this.height);
	            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);
            }
	        this.renderBg(mc, mouseX, mouseY);
            int l = 14737632;

            if (!this.enabled) {
                l = -6250336;
            }
            else if (this.hovered) {
                l = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
	}
	
	public TextColour getCurrentColour(int index) {
		if(index <= 0 || index >= TextColour.values().length)
			return TextColour.BLACK;
		return TextColour.values()[index];
	}
	
	public void leftClick() {
		textColourIndex--;
		while(textColourIndex < 0)
			textColourIndex += TextColour.values().length;
		while(textColourIndex > TextColour.values().length - 1)
			textColourIndex = 0;
	}
	
	public void rightClick() {
		textColourIndex++;
		while(textColourIndex < 0)
			textColourIndex += TextColour.values().length;
		while(textColourIndex > TextColour.values().length - 1)
			textColourIndex = 0;
	}
	
	public enum TextColour {
		
		BLACK(0, 0, 0, "0"),
		DARK_BLUE(0, 0, 170, "1"),
		DARK_GREEN(0, 170, 0, "2"),
		DARK_AQUA(0, 170, 170, "3"),
		DARK_RED(170, 0, 0, "4"),
		PURPLE(170, 0, 170, "5"),
		GOLD(255, 170, 0, "6"),
		GREY(170, 170, 170, "7"),
		DARK_GREY(85, 85, 85, "8"),
		BLUE(85, 85, 255, "9"),
		GREEN(85, 255, 85, "a"),
		AQUA(85, 255, 255, "b"),
		RED(255, 85, 85, "c"),
		LIGHT_PURPLE(255, 85, 255, "d"),
		YELLOW(255, 255, 85, "e"),
		WHITE(255, 255, 255, "f"),
		OBFUSCATED(255, 255, 255, "k"),
		BOLD(255, 255, 255, "l"),
		STRIKETHROUGHT(255, 255, 255, "m"),
		UNDERLINE(255, 255, 255, "n"),
		ITALIC(255, 255, 255, "o"),
		RESET(255, 255, 255, "r");
		
		int red, green, blue;
		String prefix;
		
		TextColour(int red, int green, int blue, String prefix) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.prefix = prefix;
		}
		
		public String getName() {
			return this.getColour() + this.name();
		}
		
		public String getColour() {
			return "\u00a7" + prefix;
		}
	}
}
