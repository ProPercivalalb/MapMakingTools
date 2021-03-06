package mapmakingtools.api.itemeditor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public abstract class IItemAttribute {

	public abstract boolean isApplicable(EntityPlayer playerIn, ItemStack itemstackIn);
	public abstract void onItemCreation(ItemStack itemstackIn, int param);
	public abstract String getUnlocalizedName();
	public String getAttributeName() { return I18n.format(this.getUnlocalizedName()); }
	public abstract void populateFromItem(IGuiItemEditor itemEditor, ItemStack itemstackIn, boolean first);
	public abstract void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height);
	public void drawGuiContainerBackgroundLayer(IGuiItemEditor itemEditor, float partialTicks, int xMouse, int yMouse) {}
	public void drawGuiContainerForegroundLayer(IGuiItemEditor itemEditor, int xMouse, int yMouse) {}
	public abstract void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height);
	public void updateScreen(IGuiItemEditor itemEditor) {}
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {}
	public void keyTyped(IGuiItemEditor itemEditor, char character, int keyId) {}
	public void mouseClicked(IGuiItemEditor itemEditor, double mouseX, double mouseY, int mouseButton) {}
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {}
	public void drawToolTips(IGuiItemEditor guiItemEditor, int xMouse, int yMouse) {}
}
