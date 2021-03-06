package mapmakingtools.command;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class BoxCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.box.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/box").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).then(Commands.argument("block", BlockStateArgument.blockState()).executes((command) -> {
			return setBlock(command.getSource(), BlockStateArgument.getBlockState(command, "block"));
		})));
	}

	private static int setBlock(CommandSource source, BlockStateInput block) throws CommandSyntaxException {
		WorldServer world = source.getWorld();
		Entity entity = source.getEntity();
		
		if(!(entity instanceof EntityPlayer))
			throw ERROR.create();
		
		EntityPlayer player = (EntityPlayer)entity;
		PlayerData data = WorldData.getPlayerData(player);
      
		if(!data.hasSelectedPoints())
			throw CommandUtil.NO_POINTS_SELECTED.create();
		
		int maxY = data.getMaxY();
		Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), maxY, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), maxY, data.getSecondPoint().getZ()));
		
  	  	int blocks = 0;
  		ArrayList<BlockCache> list = new ArrayList<BlockCache>();
  	  	
  		for(BlockPos pos : positions) {
			boolean flag1 = pos.getX() == data.getMinX();
			boolean flag2 = pos.getX() == data.getMaxX();
			boolean flag3 = pos.getZ() == data.getMinZ();
			boolean flag4 = pos.getZ() == data.getMaxZ();
			boolean flag5 = pos.getY() == data.getMinY();
			boolean flag6 = pos.getY() == data.getMaxY();
			boolean[] flags = new boolean[] {flag1, flag2, flag3, flag4, flag5, flag6};
		    for (boolean var : flags) {;
				if(var) {
					list.add(BlockCache.createCache(player, world, pos));
					if(block.place(world, pos, 2)) {
						blocks += 1;
					}
					break;
				}
		    }
		}
  	  	
		source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.build.box.success", blocks).applyTextStyle(TextFormatting.ITALIC), true);
		data.getActionStorage().addUndo(list);
		return 1;
	}
}