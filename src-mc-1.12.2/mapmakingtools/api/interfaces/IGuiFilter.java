package mapmakingtools.api.interfaces;

import java.util.List;

import mapmakingtools.api.enums.TargetType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IGuiFilter {

	public int xFakeSize();
	public int getScreenWidth();
	public int getScreenHeight();
	public int getGuiY();
	public int getGuiX();
	public IContainerFilter getFilterContainer();
	public void setYSize(int newYSize);
	public void drawHoveringTooltip(List<String> text, int mouseX, int mouseY);
	
	public TargetType getTargetType();
	public BlockPos getBlockPos();
	public int getEntityId();
	public Entity getEntity();
	
	public World getWorld();
	public EntityPlayer getPlayer();
	public List<GuiLabel> getLabelList();
	public List<GuiButton> getButtonList();
	public List<GuiTextField> getTextBoxList();
	public FontRenderer getFont();
	public void drawTexturedModalRectangle(int par1, int par2, int par3, int par4, int par5, int par6);
}
