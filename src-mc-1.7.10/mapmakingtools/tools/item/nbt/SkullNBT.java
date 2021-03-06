package mapmakingtools.tools.item.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class SkullNBT {

	public static final String SKULL_NAME = "SkullOwner";

	public static boolean checkHasTag(ItemStack item) {
		return item.hasTagCompound() && item.stackTagCompound.hasKey(SKULL_NAME, 8);
	}
	
	public static String getSkullName(ItemStack item) {
		if(!checkHasTag(item))
			return "";
		return item.stackTagCompound.getString(SKULL_NAME);
	}

	public static void setSkullName(ItemStack item, String name) {
		if(!item.hasTagCompound())
			item.setTagCompound(new NBTTagCompound());
		item.stackTagCompound.setString(SKULL_NAME, name);
	}
}
