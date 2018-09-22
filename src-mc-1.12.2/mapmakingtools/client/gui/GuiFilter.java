package mapmakingtools.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.button.ButtonType;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.client.gui.button.GuiTabSelect;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketSelectedFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class GuiFilter extends GuiContainer implements IGuiFilter {

	//Extra data the class holds
	public EntityPlayer player;
    public BlockPos pos;
    public int entityId;
	public TargetType mode;
	
	private List<FilterClient> filterList;
	private static FilterClient filterCurrent;
	private static int currentPage = 1;
	private int maxPages = 1;
	
	private List textboxList = new ArrayList();
	
	private GuiFilter(List<FilterClient> filters, EntityPlayer player) {
		super(new ContainerFilter(FilterManager.getServerFiltersFromList(filters), player));
		this.xSize = 302;
        this.ySize = 100;
        this.filterList = filters;
        this.player = player;
        this.maxPages = (this.filterList.size() + (filterList.size() % 6)) / 6;
        
        //If the last selected filter is not in this new gui reset it
        if(!this.filterList.contains(filterCurrent)) {
        	filterCurrent = null;
        	currentPage = 1;
        }
        else {
        	int index = this.filterList.indexOf(filterCurrent);
        	PacketDispatcher.sendToServer(new PacketSelectedFilter(index));
        	this.getContainerFilter().setSelected(index);
        }
	}
	
	public GuiFilter(List<FilterClient> filters, EntityPlayer player, BlockPos pos) {
		this(filters, player);
		this.pos = pos;
	    this.mode = TargetType.BLOCK;
	    this.getContainerFilter().setBlockPos(pos);
	}
	    
	public GuiFilter(List<FilterClient> filters, EntityPlayer player, int entityId) {
		this(filters, player);
	    this.entityId = entityId;
	    this.mode = TargetType.ENTITY;
	    this.getContainerFilter().setEntityId(entityId);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    	this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int xMouse, int yMouse) {
		int topX = (this.width - this.xFakeSize()) / 2;
        int topY = (this.height - this.yFakeSize()) / 2;
        
		if(filterCurrent == null || !filterCurrent.drawBackground(this)) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_SMALL);
			this.drawTexturedModalRect(topX, topY, 0, 0, this.xFakeSize(), this.yFakeSize());
		}
		
		if(filterCurrent != null)
			filterCurrent.drawGuiContainerBackgroundLayer(this, partialTicks, xMouse, yMouse);
		else {
			GlStateManager.pushMatrix();
			double scale = 1.7D;
			GlStateManager.scale(scale, scale, scale);
			this.fontRenderer.drawString("Minecraft Filters", (int)((topX + 10) / scale), (int)((topY + 15) / scale), 0);
			GlStateManager.scale(0.588D, 0.588D, 0.588D);
			GlStateManager.popMatrix();
		}
		
		for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)this.textboxList.get(i)).drawTextBox();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		int topX = (this.width - this.xFakeSize()) / 2;
        int topY = (this.height - this.yFakeSize()) / 2;
        
		if(filterCurrent != null)
			filterCurrent.drawGuiContainerForegroundLayer(this, xMouse, yMouse);
		
		GlStateManager.translate((float)-this.guiLeft, (float)-this.guiTop, 0.0F);
		for(int var1 = 0; var1 < this.buttonList.size(); ++var1) {
    		GuiButton listBt = (GuiButton)this.buttonList.get(var1);
    		if(listBt instanceof GuiTabSelect) {
        		GuiTabSelect button = (GuiTabSelect)listBt;
        		if(button.isMouseAbove(xMouse, yMouse)) {
        			List<String> list = new ArrayList<String>();
        			list.add(button.filter.getFilterName());
        			if(button.filter.showErrorIcon(this)) {
        				String errorMessage = button.filter.getErrorMessage(this);
        				if(errorMessage != null)
        					list.add(errorMessage);
        			}
        			this.drawHoveringText(list, xMouse, yMouse);
        		}
    		}
    		else if(listBt instanceof GuiSmallButton && listBt.id == 156) {
    			GuiSmallButton button = (GuiSmallButton)listBt;
        		if(button.isMouseAbove(xMouse, yMouse)) {
        			List<String> list = this.filterCurrent.getFilterInfo(this);
        			this.drawHoveringText(list, xMouse, yMouse);
        		}
    		}
    		//if(listBt instanceof GuiButtonCancel) {
    		//	GuiButtonCancel tab = (GuiButtonCancel)listBt;
        	//	if(tab.isMouseAbove(xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList(I18n.translateToLocal("gui.cancel"));
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 148) {
    		//	if(listBt.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Prev. Page");
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 149) {
    		//	if(listBt.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Next. Page");
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    	}
		if(filterCurrent != null)
			filterCurrent.drawToolTips(this, xMouse, yMouse);
		GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
    }

	@Override
    public void initGui() {
    	super.initGui();
    	Keyboard.enableRepeatEvents(true);
    	this.textboxList.clear();
    	this.buttonList.clear();
    	this.labelList.clear();
    	int topX = (this.width - this.xFakeSize()) / 2;
        int topY = (this.height - this.yFakeSize()) / 2;

        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 6) {
            this.buttonList.add(new GuiTabSelect(150, topX - 29, topY + 9, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 6), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 5) {
        	this.buttonList.add(new GuiTabSelect(151, topX - 29, topY + 36, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 5), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 4) {
        	this.buttonList.add(new GuiTabSelect(152, topX - 29, topY + 63, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 4), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 3) {
        	this.buttonList.add(new GuiTabSelect(153, topX + xFakeSize() - 1, topY + 9, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 3), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 2) {
        	this.buttonList.add(new GuiTabSelect(154, topX + xFakeSize() - 1, topY + 36, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 2), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 1) {
        	this.buttonList.add(new GuiTabSelect(155, topX + xFakeSize() - 1, topY + 63, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 1), this));
        }
        
        if(this.filterList.size() > 6) {
        	this.buttonList.add(new GuiButton(148, topX + xFakeSize() + 3,  topY - 15, 20, 20, "<"));
        	this.buttonList.add(new GuiButton(149, topX + xFakeSize() + 26, topY - 15, 20, 20, ">"));
        }

        this.setYSize(100);
        
        if(filterCurrent != null)
        	filterCurrent.initGui(this);
        else {}
        	//this.buttonList.add(new GuiButtonCancel(this, -1, k + 210, l + 70, 112, 220)); //cancel
        
        int realtopY = (this.height - this.ySize) / 2;
        
        if(filterCurrent != null && filterCurrent.getFilterInfo(this) != null)
        	this.buttonList.add(new GuiSmallButton(156, topX + 5, realtopY + 4, 13, 12, "?"));
        
        if(filterCurrent != null && filterCurrent.hasUpdateButton(this))
        	this.buttonList.add(new GuiSmallButton(157, topX + 20, realtopY + 4, 8, 8, "" + (char)8595));
	
        for(int i = 0; i < this.buttonList.size(); ++i) {
    		if(this.buttonList.get(i) instanceof GuiTabSelect) {
        		GuiTabSelect tab = (GuiTabSelect)(GuiButton)this.buttonList.get(i);
        		if(tab.filter == filterCurrent) {
        			int index = this.filterList.indexOf(filterCurrent);
        			this.getContainerFilter().setSelected(index);
        			PacketDispatcher.sendToServer(new PacketSelectedFilter(index));

        			tab.isSelected = true;
        		}
    		}
        }	
	}

	@Override
    public void updateScreen() {
    	if(filterCurrent != null)
    		filterCurrent.updateScreen(this);
    	
    	for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)this.textboxList.get(i)).updateCursorCounter();
    }
	
    @Override
    public void onGuiClosed() {
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
	public void actionPerformed(GuiButton button) {
        if(filterCurrent != null)
        	filterCurrent.actionPerformed(this, button);
    	
    	if (button.enabled) {
            switch (button.id) {
            	case -1:
            		ClientHelper.getClient().player.closeScreen();
            		break;
            
            	case 148:
            		if(this.currentPage > 1) {
            			--currentPage;
            			this.initGui();
            		}
            		break;
            	case 149:
            		if(this.currentPage <= maxPages) {
            			++currentPage;
            			this.initGui();
            		}
            		break;
                case 150:
                case 151:
                case 152:
                case 153:
                case 154:
                case 155:
                	if(button instanceof GuiTabSelect)
                		unSelectBut((GuiTabSelect)button);
                	break;
                case 157:
                	filterCurrent.updateButtonClicked(this);
                default:
            }
        }
    }
    
    @Override
    protected void keyTyped(char cha, int charIndex) {
        if(filterCurrent != null)
        	filterCurrent.keyTyped(this, cha, charIndex);
        
        if(filterCurrent == null || filterCurrent.doClosingKeysWork(this, cha, charIndex))
        	if (charIndex == Keyboard.KEY_ESCAPE)
        		ClientHelper.getClient().player.closeScreen();
        
        for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)this.textboxList.get(i)).textboxKeyTyped(cha, charIndex);
    }
    
    @Override
    protected void mouseClicked(int xMouse, int yMouse, int mouseButton) throws IOException {
        super.mouseClicked(xMouse, yMouse, mouseButton);
        
        if(filterCurrent != null)
        	filterCurrent.mouseClicked(this, xMouse, yMouse, mouseButton);
       
        for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)this.textboxList.get(i)).mouseClicked(xMouse, yMouse, mouseButton);
    }
    
    public ContainerFilter getContainerFilter() {
    	return ((ContainerFilter)this.inventorySlots);
    }
    
    public void unSelectBut(GuiTabSelect button) {
    	filterCurrent = button.filter;
    	this.initGui();
    	for(int i = 0; i < this.buttonList.size(); ++i) {
    		GuiButton listBt = (GuiButton)this.buttonList.get(i);
    		if(listBt.id == button.id) {
    			if(listBt instanceof GuiTabSelect)
    				((GuiTabSelect)listBt).isSelected = true;
    		}
    	}
    }
    
    protected void drawHoveringText(List text, int mouseX, int mouseY, FontRenderer font) {
        if(!text.isEmpty()) {
        	GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int k = 0;
            Iterator iterator = text.iterator();

            while (iterator.hasNext()) {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                    k = l;
            }

            int j2 = mouseX + 12;
            int k2 = mouseY - 12;
            int i1 = 8;

            if (text.size() > 1)
                i1 += 2 + (text.size() - 1) * 10;

            if (j2 + k > this.width)
                j2 -= 28 + k;

            if (k2 + i1 + 6 > this.height)
                k2 = this.height - i1 - 6;

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < text.size(); ++i2) {
                String s1 = (String)text.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0)
                    k2 += 2;

                k2 += 10;
            }

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
    
    
    ///////////////////////////////  IGuiFilter overridden methods  ///////////////////////////////
    
    @Override
    public int xFakeSize() {
		return 240;
	}
	
    @Override
	public int yFakeSize() {
		return 100;
	}
    
	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public int getGuiTop() {
		return this.guiTop;
	}

	@Override
	public int getGuiLeft() {
		return this.guiLeft;
	}
	
    @Override
	public void setYSize(int newYSize) {
		this.ySize = newYSize;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.pos;
	}

	@Override
	public int getEntityId() {
		return this.entityId;
	}
	
	@Override
	public World getWorld() {
		return this.player.world;
	}

	@Override
	public EntityPlayer getPlayer() {
		return this.player;
	}
    
	@Override
	public void drawHoveringText2(List<String> text, int mouseX, int mouseY) {
		this.drawHoveringText(text, mouseX, mouseY, ClientHelper.getClient().fontRenderer);
	}

	@Override
	public List getLabelList() {
		return this.labelList;
	}

	@Override
	public List getButtonList() {
		return this.buttonList;
	}

	@Override
	public List getTextBoxList() {
		return this.textboxList;
	}
	
	@Override
	public FontRenderer getFont() {
		return this.fontRenderer;
	}

	@Override
	public void drawTexturedModalRectangle(int par1, int par2, int par3, int par4, int par5, int par6) {
		this.drawTexturedModalRect(par1, par2, par3, par4, par5, par6);
	}

	@Override
	public IContainerFilter getFilterContainer() {
		return (IContainerFilter)this.inventorySlots;
	}
	
	@Override
	public Entity getEntity() {
		return this.getWorld().getEntityByID(this.getEntityId());
	}
}
