package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterMobSpawnerBase;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketItemSpawner;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

/**
 * @author ProPercivalalb
 */
public class ItemSpawnerClientFilter extends FilterMobSpawnerBase {

	public GuiButton btnOk;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.changeitem.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/change_item.png";
	}

	@Override
	public void initGui(final IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(104);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        
        this.btnOk = new GuiButton(0, topX + 20, topY + 61, 20, 20, "OK");
        gui.getButtonList().add(this.btnOk);
        this.addPotentialSpawnButtons(gui, topX, topY);
	}

	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.changeitem.info"));
	}
	
	@Override
	public void mouseClicked(IFilterGui gui, int xMouse, int yMouse, int mouseButton) {
		super.mouseClicked(gui, xMouse, yMouse, mouseButton);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
		this.removePotentialSpawnButtons(gui, xMouse, yMouse, mouseButton, (gui.getScreenWidth() - gui.xFakeSize()) / 2, gui.getGuiY());
	}
	
	@Override
	public boolean showErrorIcon(IFilterGui gui) {
		MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			
		List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawnerLogic);
		if(minecarts.size() <= 0) return true;
		WeightedSpawnerEntity randomMinecart = minecarts.get(potentialSpawnIndex);
		ResourceLocation mobId = SpawnerUtil.getMinecartType(randomMinecart);
		if(mobId.toString().equals("minecraft:item"))
			return false;
			
		return true; 
	}
	
	@Override
	public String getErrorMessage(IFilterGui gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.changeitem.error");
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            if(button.id == 0) {
                PacketDispatcher.sendToServer(new PacketItemSpawner(potentialSpawnIndex));
            	ClientHelper.getClient().player.closeScreen();
            }
        }
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_ONE_SLOT);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 104);
		return true;
	}
}
