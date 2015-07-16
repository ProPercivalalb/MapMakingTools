package mapmakingtools.integration.nei;

import mapmakingtools.lib.Reference;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

/**
 * @author ProPercivalalb
 */
public class NEIMapMakingToolsConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {	
		API.registerNEIGuiHandler(new NEIGuiHandler());
	}

	@Override
	public String getName() {
		return Reference.MOD_NAME;
	}

	@Override
	public String getVersion() {
		return Reference.MOD_VERSION;
	}

}