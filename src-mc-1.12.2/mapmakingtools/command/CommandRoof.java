package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandRoof extends CommandBase {

	@Override
	public String getName() {
		return "/roof";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.roof.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(args.length < 1)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		else {
			Block block = getBlockByText(sender, args[0]);
			int meta = 0;
			
			if(args.length == 2)
				meta = parseInt(args[1]);
			
			String displayName = block != Blocks.AIR ? new ItemStack(block, 1, meta).getDisplayName() : I18n.translateToLocal("mapmakingtools.commands.build.air");
			
			int maxY = data.getMaxY();
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			int blocks = 0;
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), maxY, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), maxY, data.getSecondPoint().getZ()));
			
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(player, world, pos));
				world.setBlockState(pos, block.getStateFromMeta(meta), 2);
				blocks += 1;
			}

			data.getActionStorage().addUndo(list);
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.roof.complete", "" + blocks, displayName);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
			
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys()) : Collections.<String>emptyList();
	}
}
