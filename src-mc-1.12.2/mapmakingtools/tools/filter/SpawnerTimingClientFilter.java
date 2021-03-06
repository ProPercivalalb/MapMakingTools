package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterMobSpawnerBase;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.Numbers;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketSpawnerTimings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

/**
 * @author ProPercivalalb
 */
public class SpawnerTimingClientFilter extends FilterMobSpawnerBase {
	
	private GuiTextField txt_minDelay;
	private GuiTextField txt_maxDelay;
	private GuiTextField txt_spawnRadius;
	private GuiTextField txt_spawnCount;
	private GuiTextField txt_entityCap;
	private GuiTextField txt_detectionRange;
	private GuiButton btn_ok;
	private GuiButton btn_cancel;
	
	private static String minDelayText = "200";
	private static String maxDelayText = "800";
	private static String spawnRadiusText = "4";
	private static String spawnCountText = "4";
	private static String entityCapText = "6";
	private static String detectionRangeText = "16";
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.spawnertimings.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/spawner_timings.png";
	}
	
	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(160);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btn_ok = new GuiButton(0, topX + 140, topY + 133, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, topX + 40, topY + 133, 60, 20, "Cancel");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        this.txt_minDelay = new GuiTextField(0, gui.getFont(), topX + 20, topY + 37, 90, 20);
        this.txt_minDelay.setMaxStringLength(7);
        this.txt_minDelay.setText(minDelayText);
        this.txt_maxDelay = new GuiTextField(1, gui.getFont(), topX + 120, topY + 37, 90, 20);
        this.txt_maxDelay.setMaxStringLength(7);
        this.txt_maxDelay.setText(maxDelayText);
        this.txt_spawnRadius = new GuiTextField(2, gui.getFont(), topX + 120, topY + 72, 90, 20);
        this.txt_spawnRadius.setMaxStringLength(7);
        this.txt_spawnRadius.setText(maxDelayText);
        this.txt_spawnCount = new GuiTextField(3, gui.getFont(), topX + 20, topY + 72, 90, 20);
        this.txt_spawnCount.setMaxStringLength(7);
        this.txt_spawnCount.setText(spawnCountText);
        this.txt_entityCap = new GuiTextField(4, gui.getFont(), topX + 20, topY + 107, 90, 20);
        this.txt_entityCap.setMaxStringLength(7);
        this.txt_entityCap.setText(entityCapText);
        this.txt_detectionRange = new GuiTextField(5, gui.getFont(), topX + 120, topY + 107, 90, 20);
        this.txt_detectionRange.setMaxStringLength(7);
        this.txt_detectionRange.setText(detectionRangeText);
        gui.getTextBoxList().add(this.txt_minDelay);
        gui.getTextBoxList().add(this.txt_maxDelay);
        gui.getTextBoxList().add(this.txt_spawnRadius);
        gui.getTextBoxList().add(this.txt_spawnCount);
        gui.getTextBoxList().add(this.txt_entityCap);
        gui.getTextBoxList().add(this.txt_detectionRange);
        
        this.addPotentialSpawnButtons(gui, topX, topY);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IFilterGui gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
	    int topY = gui.getGuiY();
        gui.getFont().drawString("Min Delay", topX + 20, topY + 27, 4210752);
        gui.getFont().drawString("Max Delay", topX + 120, topY + 27, 4210752);
        gui.getFont().drawString("Spawn Radius", topX + 20, topY + 62, 4210752);
        gui.getFont().drawString("Spawn Count", topX + 120, topY + 62, 4210752);
        gui.getFont().drawString("Entity Cap", topX + 20, topY + 97, 4210752);
        gui.getFont().drawString("Detection Range", topX + 120, topY + 97, 4210752);
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            if(button.id == 0) {
                PacketDispatcher.sendToServer(new PacketSpawnerTimings(this.txt_minDelay.getText(), this.txt_maxDelay.getText(), this.txt_spawnRadius.getText(), this.txt_spawnCount.getText(), this.txt_entityCap.getText(), this.txt_detectionRange.getText()));
            	ClientHelper.getClient().player.closeScreen();
            }
        }
	}
	
	@Override
	public void updateScreen(IFilterGui gui) {
		minDelayText = this.txt_minDelay.getText();
		maxDelayText = this.txt_maxDelay.getText();
		spawnRadiusText = this.txt_spawnRadius.getText();
		spawnCountText = this.txt_spawnCount.getText();
		entityCapText = this.txt_entityCap.getText();
		detectionRangeText = this.txt_detectionRange.getText();
		this.btn_ok.enabled = Numbers.areIntegers(minDelayText, maxDelayText, spawnRadiusText, spawnCountText, entityCapText, detectionRangeText);
	}
	
	@Override
	public void mouseClicked(IFilterGui gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		this.removePotentialSpawnButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.mobposition.info"));
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_LARGE);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 160);
		return true;
	}
}
