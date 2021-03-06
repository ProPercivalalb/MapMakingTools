package mapmakingtools.tools.filter.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.datareader.ChestSymmetrifyData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketChestSymmetrify {
	
	public BlockPos pos;
	
	public PacketChestSymmetrify(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketChestSymmetrify msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
	}
	
	public static PacketChestSymmetrify decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		return new PacketChestSymmetrify(pos);
	}
	
	public static class Handler {
        public static void handle(final PacketChestSymmetrify msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		TileEntity tile = player.world.getTileEntity(msg.pos);
        		if(tile instanceof TileEntityChest) {
        			TileEntityChest chest = (TileEntityChest)tile;
        			
        			//Gets the valid stacks from the chest and stores them in a list
        			List<ItemStack> stacksInChest = new ArrayList<ItemStack>();
        			int currentCount = 0;
        			for(int index = 0; index < chest.getSizeInventory(); ++index) {
        				ItemStack stack = chest.getStackInSlot(index);
        				if(!stack.isEmpty()) {
        					stacksInChest.add(stack);
        					chest.setInventorySlotContents(index, ItemStack.EMPTY);
        					++currentCount;
        				}
        			}
        			
        			if(stacksInChest.size() < 1) {
        				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.chestsymmetrify.nocontents");
        				chatComponent.getStyle().setItalic(true);
        				chatComponent.getStyle().setColor(TextFormatting.RED);
        				player.sendMessage(chatComponent);
        				return;
        			}	
        			
        			//Counts the amount of each id in the chest
        			Map<Integer, Integer> idCounts = new HashMap<Integer, Integer>();
        			for(ItemStack stack : stacksInChest) {
        				int id = Item.getIdFromItem(stack.getItem());
        				if(idCounts.containsKey(id)) {
        					idCounts.put(id, idCounts.get(id) + 1);
        				}
        				else {
        					idCounts.put(id, 1);
        				}
        			}
        			
        			List<ArrayList<ItemStack>> sortedItems = new ArrayList<ArrayList<ItemStack>>();
        			
        			while(idCounts.size() > 0) {
        				int maxEvenId = 0;
        				int maxEvenCount = 0;
        				int maxOddId = 0;
        				int maxOddCount = 0;
        				
        				Iterator<Integer> ite = idCounts.keySet().iterator();
        				
        				while(ite.hasNext()) {
        					int id = ite.next();
        					if(idCounts.get(id) % 2 == 0 && idCounts.get(id) > maxEvenCount) {
        						maxEvenId = id;
        						maxEvenCount = idCounts.get(id);
        					}
        					if(idCounts.get(id) % 2 == 1 && idCounts.get(id) > maxOddCount) {
        						maxOddId = id;
        						maxOddCount = idCounts.get(id);
        					}
        				}
        				
        				int maxId;	
        				if(maxEvenCount != 0) {
        					maxId = maxEvenId;
        				}
        				else {
        					maxId = maxOddId;
        				}
        				
        				ArrayList<ItemStack> group = new ArrayList<ItemStack>();
        				for(ItemStack stack : stacksInChest) {
        					if(Item.getIdFromItem(stack.getItem()) == maxId) {
        						group.add(stack);
        					}
        				}
        				
        				sortedItems.add(group);
        				idCounts.remove(maxId);	
        			}
        			
        			int[] taken = new int[27];
        			//Arrays.fill(taken, 0);
        			
        			String[][] arrangement = ChestSymmetrifyData.getPattern(stacksInChest.size());
        			
        			Map<Integer, ItemStack> newItems = new HashMap<Integer, ItemStack>();
        			
        			for(int row : new int[] {1, 0, 2}) {
        				if(arrangement[row][4].equalsIgnoreCase("x")) {
        					for(ArrayList<ItemStack> group : sortedItems) {
        						if(group.size() % 2 == 1) {
        							int qtyPlaced = 0;
        							for(ItemStack item : group) {
        								boolean placed = false;
        								for(int column : new int[] {4, 5, 3, 6, 2, 7, 1, 8, 0}) {
        									int idx = row * 9 + column;
        									if(arrangement[row][column].equalsIgnoreCase("x") && taken[idx] == 0) {
        										taken[idx] = 1;
        										newItems.put(idx, item);
        										placed = true;
        										qtyPlaced += 1;
        										break;
        									}
        								}
        								if(!placed) {
        									break;
        								}
        							}
        							for(int i = 0; i < qtyPlaced; ++i) {
        								group.remove(group.get(0));
        							}
        							break;
        							
        						}
        					}
        				}
        			}
        			
        			for(ArrayList<ItemStack> group : sortedItems) {
        				for(ItemStack item : group) {
        					boolean placed = false;
        					for(int row : new int[] {1, 0, 2}) {
        						for(int column : new int[] {4, 5, 3, 6, 2, 7, 1, 8, 0}) {
        							int idx = row * 9 + column;
        							if(arrangement[row][column].equalsIgnoreCase("x") && taken[idx] == 0) {
        								taken[idx] = 1;
        								newItems.put(idx, item);
        								placed = true;
        								break;
        							}
        						}
        						
        						if(placed) {
        							break;
        						}
        							
        					}
        				}
        			}
        			
        			for(int index = 0; index < chest.getSizeInventory(); ++index) {
        				chest.setInventorySlotContents(index, ItemStack.EMPTY);
        			}
        			
        			Iterator<Integer> ite = newItems.keySet().iterator();
        			
        			while(ite.hasNext()) {
        				int key = ite.next();
        				ItemStack item = newItems.get(key);
        				chest.setInventorySlotContents(key, item.copy());
        			}
        			
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.chestsymmetrify.complete").applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}	

}
