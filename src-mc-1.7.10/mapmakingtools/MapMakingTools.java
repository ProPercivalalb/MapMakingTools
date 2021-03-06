package mapmakingtools;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import mapmakingtools.handler.ActionHandler;
import mapmakingtools.handler.CommandHandler;
import mapmakingtools.handler.ConfigurationHandler;
import mapmakingtools.handler.EntityJoinWorldHandler;
import mapmakingtools.handler.PlayerTrackerHandler;
import mapmakingtools.handler.WorldSaveHandler;
import mapmakingtools.helper.MapMakingToolsVersion;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
public class MapMakingTools {

	@Instance(value = Reference.MOD_ID)
	public static MapMakingTools instance;
	
	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
    public static CommonProxy proxy;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	ConfigurationHandler.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
    	MapMakingToolsVersion.startVersionCheck();
    	proxy.onPreLoad();
    	PacketDispatcher.registerPackets();
    }
    
    @EventHandler
    public void onInit(FMLInitializationEvent event) {
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    	MinecraftForge.EVENT_BUS.register(new ActionHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldSaveHandler());
    	MinecraftForge.EVENT_BUS.register(new EntityJoinWorldHandler());
    	FMLCommonHandler.instance().bus().register(new PlayerTrackerHandler());
    	ModItems.inti();
    	proxy.registerFilters();  
    	proxy.registerRotation();
    	proxy.registerItemAttribute();
    	proxy.registerForceKill();
    	proxy.registerHandlers();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Initialize the custom commands
        CommandHandler.initCommands(event);
    }
	
}
