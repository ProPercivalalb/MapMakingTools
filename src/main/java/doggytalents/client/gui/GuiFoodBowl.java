package doggytalents.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import doggytalents.inventory.ContainerFoodBowl;
import doggytalents.lib.ResourceReference;
import doggytalents.tileentity.TileEntityFoodBowl;

/**
 * @author ProPercivalalb
 **/
public class GuiFoodBowl extends GuiContainer {
	
    private TileEntityFoodBowl foodBowl;

    public GuiFoodBowl(InventoryPlayer playerInventory, TileEntityFoodBowl par2TileEntityFoodBowl) {
        super(new ContainerFoodBowl(playerInventory, par2TileEntityFoodBowl));
        this.foodBowl = par2TileEntityFoodBowl;
        this.ySize = 127;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int var1, int var2) {
    	String s = this.foodBowl.hasCustomInventoryName() ? this.foodBowl.getInventoryName() : StatCollector.translateToLocal(this.foodBowl.getInventoryName());
        this.fontRendererObj.drawString(s, 10, 8, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ResourceReference.foodBowl);
        int var2 = (this.width - this.xSize) / 2;
        int var3 = (this.height - this.ySize) / 2;
        drawTexturedModalRect(var2, var3, 0, 0, this.xSize, this.ySize);
    }
}
