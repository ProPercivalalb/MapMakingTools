package doggytalents;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import doggytalents.addon.AddonManager;
import doggytalents.api.DoggyTalentsAPI;
import doggytalents.api.inferface.DefaultDogBedIcon;
import doggytalents.api.registry.DogBedRegistry;
import doggytalents.api.registry.TalentRegistry;
import doggytalents.creativetab.CreativeTabDoggyTalents;
import doggytalents.handler.ConfigurationHandler;
import doggytalents.handler.ConnectionHandler;
import doggytalents.handler.EntityInteractHandler;
import doggytalents.helper.DoggyTalentsVersion;
import doggytalents.lib.Reference;
import doggytalents.network.NetworkManager;
import doggytalents.proxy.CommonProxy;
import doggytalents.talent.BedFinder;
import doggytalents.talent.BlackPelt;
import doggytalents.talent.CreeperSweeper;
import doggytalents.talent.DoggyDash;
import doggytalents.talent.FisherDog;
import doggytalents.talent.GuardDog;
import doggytalents.talent.HappyEater;
import doggytalents.talent.HellHound;
import doggytalents.talent.HunterDog;
import doggytalents.talent.PackPuppy;
import doggytalents.talent.PestFighter;
import doggytalents.talent.PillowPaw;
import doggytalents.talent.PoisonFang;
import doggytalents.talent.PuppyEyes;
import doggytalents.talent.QuickHealer;
import doggytalents.talent.RescueDog;
import doggytalents.talent.ShepherdDog;
import doggytalents.talent.WolfMount;

/**
 * @author ProPercivalalb
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
public class DoggyTalentsMod {

	@Instance(value = Reference.MOD_ID)
	public static DoggyTalentsMod instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;
	
	public static NetworkManager NETWORK_MANAGER;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
		DoggyTalentsVersion.startVersionCheck();
		DoggyTalentsAPI.CREATIVE_TAB = new CreativeTabDoggyTalents();
		ModBlocks.inti();
		ModItems.inti();
		ModEntities.inti();
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		NETWORK_MANAGER = new NetworkManager();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		MinecraftForge.EVENT_BUS.register(new EntityInteractHandler());
		FMLCommonHandler.instance().bus().register(new ConnectionHandler());
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		DoggyTalentsAPI.PACKPUPPY_BLACKLIST.registerItem(ModItems.throwBone);
		DoggyTalentsAPI.BREED_WHITELIST.registerItem(ModItems.breedingBone);
		DoggyTalentsAPI.BEG_WHITELIST.registerItem(Items.bone);
		DoggyTalentsAPI.BEG_WHITELIST.registerItem(ModItems.throwBone);
		DoggyTalentsAPI.BEG_WHITELIST.registerItem(ModItems.trainingTreat);
		DoggyTalentsAPI.BEG_WHITELIST.registerItem(ModItems.masterTreat);
		DoggyTalentsAPI.BEG_WHITELIST.registerItem(ModItems.superTreat);
		DoggyTalentsAPI.BEG_WHITELIST.registerItem(ModItems.direTreat);
		
		DogBedRegistry.CASINGS.registerMaterial(Blocks.planks, 0);
		DogBedRegistry.CASINGS.registerMaterial(Blocks.planks, 1);
		DogBedRegistry.CASINGS.registerMaterial(Blocks.planks, 2);
		DogBedRegistry.CASINGS.registerMaterial(Blocks.planks, 3);
		DogBedRegistry.CASINGS.registerMaterial(Blocks.planks, 4);
		DogBedRegistry.CASINGS.registerMaterial(Blocks.planks, 5);
		
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 0);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 1);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 2);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 3);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 4);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 5);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 6);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 7);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 8);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 9);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 10);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 11);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 12);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 13);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 14);
		DogBedRegistry.BEDDINGS.registerMaterial(Blocks.wool, 15);
		
		TalentRegistry.registerTalent(new BedFinder());
		TalentRegistry.registerTalent(new BlackPelt());
		TalentRegistry.registerTalent(new CreeperSweeper());
		TalentRegistry.registerTalent(new DoggyDash());
		TalentRegistry.registerTalent(new FisherDog());
		TalentRegistry.registerTalent(new GuardDog());
		TalentRegistry.registerTalent(new HappyEater());
		TalentRegistry.registerTalent(new HellHound());
		TalentRegistry.registerTalent(new HunterDog());
		TalentRegistry.registerTalent(new PackPuppy());
		TalentRegistry.registerTalent(new PestFighter());
		TalentRegistry.registerTalent(new PillowPaw());
		TalentRegistry.registerTalent(new PoisonFang());
		TalentRegistry.registerTalent(new PuppyEyes());
		TalentRegistry.registerTalent(new QuickHealer());
		TalentRegistry.registerTalent(new RescueDog());
		TalentRegistry.registerTalent(new ShepherdDog());
		TalentRegistry.registerTalent(new WolfMount());
		
		GameRegistry.addRecipe(new ItemStack(ModItems.throwBone, 1), new Object[] {" X ", "XYX", " X ", 'X', Items.bone, 'Y', Items.slime_ball});
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.throwBone, 1, 0), new Object[] {new ItemStack(ModItems.throwBone, 1, 0)});
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.superTreat, 5), new Object[] { new ItemStack(ModItems.trainingTreat, 1), new ItemStack(ModItems.trainingTreat, 1), new ItemStack(ModItems.trainingTreat, 1), new ItemStack(ModItems.trainingTreat, 1), new ItemStack(ModItems.trainingTreat, 1), new ItemStack(Items.golden_apple, 1)});
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.direTreat, 1), new Object[] {new ItemStack(ModItems.masterTreat, 1), new ItemStack(ModItems.masterTreat, 1), new ItemStack(ModItems.masterTreat, 1), new ItemStack(ModItems.masterTreat, 1), new ItemStack(ModItems.masterTreat, 1), new ItemStack(Blocks.end_stone, 1)});
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.breedingBone, 2), new Object[] {new ItemStack(ModItems.masterTreat, 1), new ItemStack(Items.cooked_beef, 1), new ItemStack(Items.cooked_porkchop, 1), new ItemStack(Items.cooked_chicken, 1), new ItemStack(Items.cooked_fished, 1)});
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.masterTreat, 5), new Object[] {new ItemStack(ModItems.superTreat, 1), new ItemStack(ModItems.superTreat, 1), new ItemStack(ModItems.superTreat, 1), new ItemStack(ModItems.superTreat, 1), new ItemStack(ModItems.superTreat, 1), new ItemStack(Items.diamond, 1)});
        GameRegistry.addRecipe(new ItemStack(ModItems.trainingTreat, 1), new Object[] {"TUV", "XXX", "YYY", 'T', Items.string, 'U', Items.bone, 'V', Items.gunpowder, 'X', Items.sugar, 'Y', Items.wheat});
        GameRegistry.addRecipe(new ItemStack(ModItems.collarShears, 1), new Object[] {" X ", "XYX", " X ", 'X', Items.bone, 'Y', Items.shears});
        GameRegistry.addRecipe(new ItemStack(ModItems.commandEmblem, 1), new Object[] {" X ", "XYX", " X ", 'X', Items.gold_ingot, 'Y', Items.bow});
        GameRegistry.addRecipe(new ItemStack(ModBlocks.foodBowl, 1), new Object[] {"XXX", "XYX", "XXX", 'X', Items.iron_ingot, 'Y', Items.bone});
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dogBath, 1), new Object[] {"XXX", "XYX", "XXX", 'X', Items.iron_ingot, 'Y', Items.water_bucket});
		
        GameRegistry.addRecipe(new ItemStack(ModItems.radioCollar, 1), new Object[] {"XX", "YX", 'X', Items.iron_ingot, 'Y', Items.redstone});
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.radar, 1), new Object[] {new ItemStack(Items.map, 1), new ItemStack(Items.redstone, 1), new ItemStack(ModItems.radioCollar, 1)});
		
		
		AddonManager.registerAddons();
		AddonManager.runRegisteredAddons(ConfigurationHandler.configuration);
		proxy.postInit();
	}
}
