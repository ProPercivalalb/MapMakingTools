package mapmakingtools.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.ModItems;
import mapmakingtools.client.render.RenderUtil;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.datareader.BlockList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 **/
public class ScreenRenderHandler {
	
	private int INDEX_HISTORY = 1;
	private boolean hasButtonBeenUp = true;
	public boolean isHelperOpen = false;
	public RenderItem renderer = ClientHelper.mc.getRenderItem();
	public Field chatField = ReflectionHelper.getField(GuiChat.class, 4); // inputField where you enter commands
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		//Event variables
		float partialTicks = event.getPartialTicks();
	    ScaledResolution resolution = event.getResolution();
	    ElementType type = event.getType();
		
	    EntityPlayer player = ClientHelper.mc.player;
	    World world = player.world;
	    //TODO
		ItemStack stack = player.getHeldItemMainhand();
	    
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        int mouseX = Mouse.getX() * width / ClientHelper.mc.displayWidth;
        int mouseY = height - Mouse.getY() * height / ClientHelper.mc.displayHeight - 1;
		
		if(type == ElementType.HELMET && stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 0 && PlayerAccess.canEdit(ClientHelper.mc.player)) {
    		
    		PlayerData data = ClientData.playerData;
    		FontRenderer font = ClientHelper.mc.fontRendererObj;
    		GL11.glPushMatrix();
    		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            
            if(data.hasSelectedPoints()) {
        		int[] size = data.getSelectionSize();
            	font.drawStringWithShadow(String.format("Selection Size: %d * %d * %d = %d", size[0], size[1], size[2], data.getBlockCount()), 4, 4, -1);
            }
            else
            	font.drawStringWithShadow("Nothing Selected", 4, 4, -1);
            	
    		GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    		GL11.glPopMatrix();
		}

		if(type == ElementType.HELMET && ClientHelper.mc.currentScreen == null && stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 1 && PlayerAccess.canEdit(player)) {
			GL11.glPushMatrix();
    		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
			RayTraceResult objectMouseOver = ClientHelper.mc.objectMouseOver;
			
			if(objectMouseOver != null) {
	        	List<String> list = new ArrayList<String>();
				
				if(objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
	            	
	            	IBlockState state = world.getBlockState(objectMouseOver.getBlockPos());
	            	ResourceLocation resourceLocation = (ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock());
	            	String id = resourceLocation.toString();
	            	int meta = state.getBlock().getMetaFromState(state);
	            	int stateId = Block.getStateId(state);
	            	
	            	list.add(TextFormatting.YELLOW + "" + id + " | " + meta + " | " + stateId);
	        	    
	            	ModContainer container = Loader.instance().getIndexedModList().get(resourceLocation.getResourceDomain());
	        	    if(container != null)
	        	    	list.add(TextFormatting.ITALIC + "" + container.getName());
	        	    else if(resourceLocation.getResourceDomain().equals("minecraft"))
	        	    	list.add(TextFormatting.ITALIC + "Minecraft");
	        	    else
	        	    	list.add(TextFormatting.ITALIC + "" + TextFormatting.RED + "Unknown");
	        	    
				}
				else if(objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
	           	
		        	Entity entity = objectMouseOver.entityHit;
		        	String id = EntityList.getEntityString(entity);

		            if(id == null && entity instanceof EntityPlayer)
		            	id = "Player";
		            else if(id == null && entity instanceof EntityLightningBolt)
		            	id = "LightningBolt";
		            
		        	list.add(TextFormatting.YELLOW + id + " | " + entity.getEntityId());
		        	
		        	int i = id.indexOf(".");
		        	if(i >= 0) {
		        		String domain = id.substring(0, i);
		        		ModContainer container = Loader.instance().getIndexedModList().get(domain);
		        	    if(container != null)
		        	    	list.add(TextFormatting.ITALIC + "" + container.getName());
		        	    else
		        	    	list.add(TextFormatting.ITALIC + "" + TextFormatting.RED + "Unknown");
		        	}
		        	else
		        		list.add(TextFormatting.ITALIC + "Minecraft");
	           	
				}
				
            	RenderUtil.drawHoveringText(list, 0, 25, 1000, 200, ClientHelper.mc.fontRendererObj, false);
            	GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        		GL11.glPopMatrix();
			}
		}
		
	    if(Keyboard.isKeyDown(ClientHelper.mc.gameSettings.keyBindSneak.getKeyCode()) && PlayerAccess.canSeeBlockIdHelper(player)) {
	    	if(type == RenderGameOverlayEvent.ElementType.HELMET) {
	    		this.isHelperOpen = true;
	    		if(ClientHelper.mc.currentScreen instanceof GuiChat) {
	    			GuiChat chat = (GuiChat)ClientHelper.mc.currentScreen;
	    			int chatPostion = ReflectionHelper.getField(chatField, GuiTextField.class, chat).getCursorPosition();
	    			boolean isHovering = false;

	    			float scale = 1F; //1.0F is normal size
	    			
	    			
	    			int totalWidth = MathHelper.floor((double)(width - 8) / 16.0D);
	    			int totalHeight = MathHelper.ceil((double)BlockList.getListSize() / (double)totalWidth);
	    			int renderOffset = (width - 8 - totalWidth * 16) / 2;
	    			
	    			GL11.glPushMatrix();
	    			GL11.glEnable(GL11.GL_SCISSOR_TEST);
	    			GL11.glDisable(GL11.GL_TEXTURE_2D);
	    			this.clipToSize(2, 2, width - 4, height - 20, resolution);
	    			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
	    			RenderUtil.drawTexturedModalRect(2, 2, 0, 0, width - 4, 4 + totalHeight * 16);
	    			GL11.glEnable(GL11.GL_TEXTURE_2D);
                	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            		GL11.glScalef(scale, scale, scale);
            		RenderHelper.enableGUIStandardItemLighting();
                	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                	this.renderer.zLevel = 200F;
	    			
	    			int row = 0;
	    			int column = 0;
	    			for(int i = 0; i < BlockList.getListSize(); ++i) {
	    				ItemStack item = BlockList.getList().get(i);
	    				
	    				

	    				if(item == null || item.getItem() == null)
	    					continue;
	    				
	    				if(column >= totalWidth) {
	    					row += 1;
	    					column = 0;
	    				}


	                	if(mouseX > 4 + 16 * column + renderOffset && mouseX < 4 + 16 * (column + 1) + renderOffset && mouseY > 4 + 16 * row && mouseY < 4 + 16 * (row + 1)) {
	                		GL11.glDisable(GL11.GL_TEXTURE_2D);
	                		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	                		RenderUtil.drawTexturedModalRect(4 + 16 * column + renderOffset, 4 + 16 * row, 0, 0, 16, 16);
	                		GL11.glEnable(GL11.GL_TEXTURE_2D);

		    				if((Mouse.isButtonDown(1) || Mouse.isButtonDown(0)) && hasButtonBeenUp) {
				    			String txtToInsert = String.format((ReflectionHelper.getField(chatField, GuiTextField.class, chat).getText().endsWith(" ") ? "" : " ") +"%s %s ", Block.REGISTRY.getNameForObject(Block.getBlockFromItem(item.getItem())), item.getItemDamage());
				    			for(int s = 0; s < txtToInsert.length(); s++) {
				    				char ch = txtToInsert.charAt(s);
				    				ReflectionHelper.getField(chatField, GuiTextField.class, chat).textboxKeyTyped(ch, Integer.valueOf(ch));
				    			}
				    			ReflectionHelper.getField(chatField, GuiTextField.class, chat).setCursorPosition(chatPostion + txtToInsert.length());
				    			hasButtonBeenUp = false;
		    				}
	                	}
	                	if(!Mouse.isButtonDown(1) && !Mouse.isButtonDown(0)) {
		    				hasButtonBeenUp = true;
		    			}
	                	this.renderer.renderItemIntoGUI(item, 4 + 16 * column + renderOffset, 4 + 16 * row);

	    				column++;
	    			}
	    			
	    			row = 0;
	    			column = 0;

	    			GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    			for(int i = 0; i < BlockList.getListSize(); ++i) {
	    				ItemStack item = BlockList.getList().get(i);
	    				if(item == null || item.getItem() == null)
	    					continue;
	    				if(column >= totalWidth) {
	    					row += 1;
	    					column = 0;
	    				}
	    				
	    				if(mouseX > 4 + 16 * column + renderOffset && mouseX < 4 + 16 * (column + 1) + renderOffset && mouseY > 4 + 16 * row && mouseY < 4 + 16 * (row + 1))
	    					RenderUtil.drawHoveringText(Arrays.asList(TextFormatting.GREEN + item.getDisplayName(), TextFormatting.ITALIC + String.format("%s %s", Block.REGISTRY.getNameForObject(Block.getBlockFromItem(item.getItem())), item.getItemDamage())), mouseX, mouseY, width, height, ClientHelper.mc.fontRendererObj, true);
	    				column++;
	    			}
	    			
	            	RenderHelper.disableStandardItemLighting();
	    			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    			GL11.glPopMatrix();
	            }
	    	}
	    }
	    else
	    	this.isHelperOpen = false;
	}
	
	public int chatOffset = 0;
	
	@SubscribeEvent
	public void chatEventPre(RenderGameOverlayEvent.Chat event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
		//isHelperOpen = Mouse.isButtonDown(0);
		if(ClientHelper.mc.currentScreen instanceof GuiChat) {
			if(this.isHelperOpen) {
				event.setPosY(event.getPosY() + 10000);
			}
		}
	}
	
	@SubscribeEvent
	public void chatEventPost(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
	}
	
	public void clipToSize(int xPosition, int yPosition, int width, int height, ScaledResolution scaling) {
		int scaleFactor = scaling.getScaleFactor();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(xPosition * scaleFactor, (scaling.getScaledHeight() - (yPosition + height)) * scaleFactor, width * scaleFactor, height * scaleFactor);
	}
}
