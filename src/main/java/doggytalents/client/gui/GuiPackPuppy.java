package doggytalents.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import doggytalents.entity.EntityDog;
import doggytalents.inventory.ContainerPackPuppy;
import doggytalents.inventory.InventoryPackPuppy;
import doggytalents.lib.ResourceReference;

/**
 * @author ProPercivalalb
 */
public class GuiPackPuppy extends GuiContainer {
	
    private EntityDog dog;
    private InventoryPackPuppy inventory;

    public GuiPackPuppy(InventoryPlayer inventoryplayer, EntityDog dog) {
        super(new ContainerPackPuppy(inventoryplayer, dog));
        this.dog = dog;
        this.inventory = (InventoryPackPuppy)this.dog.objects.get("packpuppyinventory");
        this.allowUserInput = false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    	String s = this.inventory.hasCustomInventoryName() ? this.inventory.getInventoryName() : StatCollector.translateToLocal(this.inventory.getInventoryName());
        this.fontRendererObj.drawString(s, this.xSize / 2 - 10, 14, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 95 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int xMouse, int yMouse) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ResourceReference.packPuppy);
        int l = (this.width - this.xSize) / 2;
        int i1 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(l, i1, 0, 0, this.xSize, this.ySize);

        for (int j1 = 0; j1 < 3; j1++)
            for (int k1 = 0; k1 < MathHelper.clamp_int(this.dog.talents.getLevel("packpuppy"), 0, 5); k1++)
            	this.drawTexturedModalRect(l + 78 + 18 * k1, i1 + 9 + 18 * j1 + 15, 197, 2, 18, 18);

        GuiInventory.func_147046_a(l + 42, i1 + 51, 30, (float)(l + 51) - xMouse, (float)((i1 + 75) - 50) - yMouse, this.dog);
    }
}
