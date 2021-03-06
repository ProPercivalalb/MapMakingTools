package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.helper.ArrayUtil;
import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

/**
 * @author ProPercivalalb
 */
public class ArmourColourAttribute extends IItemAttribute {

	private GuiTextField fld_colourint;
	private GuiTextField fld_colourhex;
	private GuiButton btn_remove;
	private String colourint;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.LEATHER_BOOTS || stack.getItem() == Items.LEATHER_CHESTPLATE || stack.getItem() == Items.LEATHER_HELMET || stack.getItem() == Items.LEATHER_LEGGINGS;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		switch(data) {
		case 0:
			if(!Numbers.isInteger(this.colourint)) break;
			
			NBTTagCompound nbttagcompound = stack.getOrCreateChildTag("display");
			nbttagcompound.putInt("color", Numbers.parse(this.colourint));
			
			break;
		case 1:
			NBTUtil.removeFromSubCompound(stack, "display", NBTUtil.ID_INTEGER, "color");
			NBTUtil.hasEmptyTagCompound(stack, true);
			
			break;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.armorcolour.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first) {
			if(NBTUtil.hasTagInSubCompound(stack, "display", "color", NBTUtil.ID_INTEGER)) {
				int integer = stack.getTag().getCompound("display").getInt("color");
				this.fld_colourint.setText(String.valueOf(integer));
				this.fld_colourhex.setText(Integer.toHexString(integer));
				this.btn_remove.enabled = true;
			}
		}
		if(!NBTUtil.hasTagInSubCompound(stack, "display", "color", NBTUtil.ID_INTEGER)) {
			this.fld_colourint.setText("");
			this.fld_colourhex.setText("");
			this.btn_remove.enabled = false;
	    }
		
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString("Integer", x + 2, y + 17, -1);
		itemEditor.getFontRenderer().drawString("Hexadecimal", x + 86, y + 17, -1);
	}

	private char[] HEX_CHARACTERS = "0123456789abcdefABCDEF".toCharArray();
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_colourint = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 28, 80, 13);
		this.fld_colourhex = new GuiTextField(1, itemEditor.getFontRenderer(), x + 86, y + 28, 80, 13) {
		    @Override
			public boolean charTyped(char typedChar, int keyCode) {
		    	
		    	if(ArrayUtil.contains(HEX_CHARACTERS, typedChar) || 14 == keyCode || 203 == keyCode || 205 == keyCode)
		    		return super.charTyped(typedChar, keyCode);
		    	return false;
		    }
		};
		this.btn_remove = new GuiButton(0, x + 2, y + 45, 80, 20, "Remove Colour") {
			@Override
	    	public void onClick(double mouseX, double mouseY) {
				itemEditor.sendUpdateToServer(1);
			}
		};
		this.fld_colourhex.setMaxStringLength(6);
		this.fld_colourint.setMaxStringLength(8);
		itemEditor.addTextFieldToGui(this.fld_colourint);
		itemEditor.addTextFieldToGui(this.fld_colourhex);
		itemEditor.addButtonToGui(this.btn_remove);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_colourint) {
			this.colourint = this.fld_colourint.getText();
			
			if(!Strings.isNullOrEmpty(this.colourint) && Numbers.isInteger(this.colourint)) {
				int integer = Numbers.parse(this.colourint);
				integer = MathHelper.clamp(integer, 0, 16777215);

				this.fld_colourint.setText(String.valueOf(integer));
				this.colourint = this.fld_colourint.getText();
				this.fld_colourhex.setText(Integer.toHexString(integer));
				
				this.btn_remove.enabled = true;
			}
			
			itemEditor.sendUpdateToServer(0);
		}
		if(textbox == this.fld_colourhex) {
			String colourhex = this.fld_colourhex.getText();
			if(!Strings.isNullOrEmpty(colourhex)) {
				this.colourint = String.valueOf(Integer.valueOf(colourhex, 16));
				this.fld_colourint.setText(this.colourint);
				this.btn_remove.enabled = true;
			}
			itemEditor.sendUpdateToServer(0);
		}
	}
}
