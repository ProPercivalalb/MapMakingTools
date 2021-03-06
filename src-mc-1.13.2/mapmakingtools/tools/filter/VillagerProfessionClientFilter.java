package mapmakingtools.tools.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.filter.FilterMobSpawnerBase;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.filter.packet.PacketVillagerProfession;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.GameData;

/**
 * @author ProPercivalalb
 */
public class VillagerProfessionClientFilter extends FilterMobSpawnerBase {

	public ScrollMenu<ResourceLocation> menu;
	public RegistryNamespaced<VillagerProfession> REGISTRY = GameData.getWrapper(VillagerProfession.class);
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.villagerprofession.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/villager_profession.png";
	}
	
	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}
	
	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(135);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.menu = new ScrollMenu<ResourceLocation>((GuiScreen)gui, topX + 8, topY + 19, 227, 108, 2, this.getProfesionList()) {

			@Override
			public void onSetButton() {
				PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketVillagerProfession(REGISTRY.get(this.getRecentSelection()), FilterMobSpawnerBase.potentialSpawnIndex));
        		ClientHelper.getClient().player.closeScreen();
			}
        	
        };
    	gui.addListenerToGui(this.menu);
        this.menu.initGui();
        
        if(SpawnerUtil.isSpawner(gui)) {
        	this.addPotentialSpawnButtons(gui, topX, topY);
        	
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			
			int prof = SpawnerUtil.getVillagerProfession(spawnerLogic, FilterMobSpawnerBase.potentialSpawnIndex);
			this.menu.setSelected(VillagerRegistry.getById(prof).getRegistryName());
		}
		else if(gui.getTargetType() == TargetType.ENTITY) {
			Entity entity = gui.getEntity();
			if(entity instanceof EntityVillager) {
				EntityVillager villager = (EntityVillager)gui.getEntity();
				int prof = villager.getProfession();
				this.menu.setSelected(VillagerRegistry.getById(prof).getRegistryName());
			}
		}
	}
	
	@Override
	public void onPotentialSpawnChange(IFilterGui gui) {
		if(SpawnerUtil.isSpawner(gui)) {
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			
			int prof = SpawnerUtil.getVillagerProfession(spawnerLogic, FilterMobSpawnerBase.potentialSpawnIndex);
			this.menu.setSelected(VillagerRegistry.getById(prof).getRegistryName());
		}
	}
	
	private List<ResourceLocation> getProfesionList() {
		List<ResourceLocation> list = new ArrayList<ResourceLocation>();
		Iterator<VillagerProfession> iterator = REGISTRY.iterator();
		
		while(iterator.hasNext())
			list.add(iterator.next().getRegistryName());
		return list;
	}

	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.villagerprofession.info"));
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IFilterGui baseIn, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(baseIn, partialTicks, xMouse, yMouse);
        this.menu.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void mouseClicked(IFilterGui gui, double mouseX, double mouseY, int mouseButton) {
		super.mouseClicked(gui, mouseX, mouseY, mouseButton);
		this.menu.mouseClicked(mouseX, mouseY, mouseButton);
		if(SpawnerUtil.isSpawner(gui))
        	this.removePotentialSpawnButtons(gui, mouseX, mouseY, mouseButton, (gui.getScreenWidth() - gui.xFakeSize()) / 2, gui.getGuiY());
	}
	
	@Override
	public boolean showErrorIcon(IFilterGui gui) {
		if(SpawnerUtil.isSpawner(gui)) {
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			
			List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawnerLogic);
			if(minecarts.size() <= potentialSpawnIndex) return true;
			WeightedSpawnerEntity randomMinecart = minecarts.get(potentialSpawnIndex);
			String mobId = SpawnerUtil.getMinecartType(randomMinecart).toString();
			if(mobId.equals("minecraft:villager"))
				return false;
		
			return true; 
		}
		
		return false;
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_SCROLL);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 135);
		return true;
	}
}
