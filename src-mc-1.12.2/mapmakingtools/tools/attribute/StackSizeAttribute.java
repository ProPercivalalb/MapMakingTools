package mapmakingtools.tools.attribute;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.helper.Numbers;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class StackSizeAttribute extends IItemAttribute {

	private GuiTextField fld_size;
	private String stacksize;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(this.stacksize == null)
			return;
		
		if(Numbers.isInteger(this.stacksize))
			stack.setCount(Numbers.parse(this.stacksize));
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.stacksize.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first)
			this.fld_size.setText(String.valueOf(stack.getCount()));
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_size = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 15, 80, 13);
		itemEditor.getTextBoxList().add(this.fld_size);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_size) {
			this.stacksize = this.fld_size.getText();
			itemEditor.sendUpdateToServer(-1);
		}
	}
}
