package mapmakingtools;

import mapmakingtools.handler.ActionHandler;
import mapmakingtools.handler.CommandHandler;
import mapmakingtools.handler.ConfigurationHandler;
import mapmakingtools.handler.EntityJoinWorldHandler;
import mapmakingtools.handler.PlayerTrackerHandler;
import mapmakingtools.handler.WorldSaveHandler;
import mapmakingtools.helper.MapMakingToolsVersion;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.NetworkManager;
import mapmakingtools.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
public class MapMakingTools {

	@Instance(value = Reference.MOD_ID)
	public static MapMakingTools instance;
	
	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
    public static CommonProxy proxy;
	
	public static NetworkManager NETWORK_MANAGER;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	ConfigurationHandler.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
    	MapMakingToolsVersion.startVersionCheck();
    	proxy.onPreLoad();
    }
    
    @EventHandler
    public void onInit(FMLInitializationEvent event) {
    	NETWORK_MANAGER = new NetworkManager();
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
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
    	proxy.onPostLoad();
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Initialize the custom commands
        CommandHandler.initCommands(event);
    }
	
}