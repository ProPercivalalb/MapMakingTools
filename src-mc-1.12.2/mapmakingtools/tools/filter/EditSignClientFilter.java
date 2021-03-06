package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonColourBlock;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.client.gui.textfield.GuiTextFieldNonInteractable;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketSignEdit;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class EditSignClientFilter extends FilterClient {

	private GuiButtonColourBlock btnColourLine1;
	private GuiTextFieldNonInteractable txtLine1;
	private GuiTextFieldNonInteractable txtLine2;
	private GuiTextFieldNonInteractable txtLine3;
	private GuiTextFieldNonInteractable txtLine4;
	private GuiButton btnInsert;
	private GuiButton btnOk;
	    
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.signedit.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/sign_edit.png";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = FakeWorldManager.getTileEntity(world, pos);
		if(tileEntity != null && tileEntity instanceof TileEntitySign)
			return true;
		return super.isApplicable(player, world, pos);
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
	    this.btnColourLine1 = new GuiButtonColourBlock(0, topX + 25, topY + 22, 20, 20);
	    this.btnInsert = new GuiButton(1, topX + 15, topY + 46, 40, 20, "Insert");
	    this.btnOk = new GuiButtonSmall(2, topX + (gui.xFakeSize() / 2) - (40 / 2), topY + 80, 40, 16, "Set");
	    this.txtLine1 = new GuiTextFieldNonInteractable(0, gui.getFont(), topX + 70, topY + 22, 100, 12);
	    this.txtLine1.setMaxStringLength(15);
	    this.txtLine2 = new GuiTextFieldNonInteractable(1, gui.getFont(), topX + 70, topY + 37, 100, 12);
	    this.txtLine2.setMaxStringLength(15);
	    this.txtLine3 = new GuiTextFieldNonInteractable(2, gui.getFont(), topX + 70, topY + 52, 100, 12);
	    this.txtLine3.setMaxStringLength(15);
	    this.txtLine4 = new GuiTextFieldNonInteractable(3,gui.getFont(), topX + 70, topY + 67, 100, 12);
	    this.txtLine4.setMaxStringLength(15);
	    gui.getButtonList().add(this.btnColourLine1);
	    gui.getButtonList().add(this.btnInsert);
	    gui.getButtonList().add(this.btnOk);
	    gui.getTextBoxList().add(this.txtLine1);
	    gui.getTextBoxList().add(this.txtLine2);
	    gui.getTextBoxList().add(this.txtLine3);
	    gui.getTextBoxList().add(this.txtLine4);
	    TileEntity tileEntity = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(tileEntity != null && tileEntity instanceof TileEntitySign) {
			TileEntitySign sign = (TileEntitySign)tileEntity;
			this.txtLine1.setText(sign.signText[0].getUnformattedText());
			this.txtLine2.setText(sign.signText[1].getUnformattedText());
			this.txtLine3.setText(sign.signText[2].getUnformattedText());
			this.txtLine4.setText(sign.signText[3].getUnformattedText());
		}
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
			if(button instanceof GuiButtonColourBlock) {
				if(this.txtLine1.isFocused()) {
            		txtLine1.missMouseClick = true;
            	}
            	else if(this.txtLine2.isFocused()) {
            		txtLine2.missMouseClick = true;
            	}
            	else if(this.txtLine3.isFocused()) {
            		txtLine3.missMouseClick = true;
            	}
            	else if(this.txtLine4.isFocused()) {
            		txtLine4.missMouseClick = true;
            	}
               	((GuiButtonColourBlock)button).leftClick();
            }
            switch (button.id) {
                case 0:
                	break;
                	//PacketTypeHandler.populatePacketAndSendToServer(new PacketConvertToDispenser(gui.x, gui.y, gui.z));
                	//ClientHelper.getClient().displayGuiScreen(null);
                   // ClientHelper.getClient().setIngameFocus();
                case 1:
                	if(this.txtLine1.isFocused()) {
                		String text = txtLine1.getText();
                		txtLine1.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine1.missMouseClick = true;
                	}
                	else if(this.txtLine2.isFocused()) {
                		String text = txtLine2.getText();
                		txtLine2.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine2.missMouseClick = true;
                	}
                	else if(this.txtLine3.isFocused()) {
                		String text = txtLine3.getText();
                		txtLine3.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine3.missMouseClick = true;
                	}
                	else if(this.txtLine4.isFocused()) {
                		String text = txtLine4.getText();
                		txtLine4.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine4.missMouseClick = true;
                	}
                	break;
                case 2:
                	PacketDispatcher.sendToServer(new PacketSignEdit(gui.getBlockPos(), new ITextComponent[] {new TextComponentString(this.txtLine1.getText()), new TextComponentString(this.txtLine2.getText()), new TextComponentString(this.txtLine3.getText()), new TextComponentString(this.txtLine4.getText())}));
                	ClientHelper.getClient().player.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public void updateScreen(IFilterGui gui) {
		
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.signedit.info"));
	}
}
