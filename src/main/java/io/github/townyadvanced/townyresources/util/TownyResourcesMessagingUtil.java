package io.github.townyadvanced.townyresources.util;

import com.meowj.langutils.lang.LanguageHelper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.util.Colors;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.townyadvanced.townyresources.TownyResources;
import io.github.townyadvanced.townyresources.objects.ResourceExtractionCategory;
import io.github.townyadvanced.townyresources.objects.ResourceOfferCategory;
import io.github.townyadvanced.townyresources.settings.TownyResourcesSettings;
import io.github.townyadvanced.townyresources.settings.TownyResourcesTranslation;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.type.NameData;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TownyResourcesMessagingUtil {

    final static String prefix = TownyResourcesTranslation.of("plugin_prefix");
    
    public static void sendErrorMsg(CommandSender sender, String message) {
        //Ensure the sender is not null (i.e. is an online player who is not an npc)
        if(sender != null)
            sender.sendMessage(prefix + Colors.Red + message);
    }

    public static void sendMsg(CommandSender sender, String message) {
        //Ensure the sender is not null (i.e. is an online player who is not an npc)
        if(sender != null)
            sender.sendMessage(prefix + Colors.White + message);
    }
    
    public static void sendGlobalMessage(String message) {
        TownyResources.info(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null && TownyAPI.getInstance().isTownyWorld(player.getWorld()))
                sendMsg(player, message);
        }
    }

    /**
     *  Format resource string to something we can send to chat utils
     *
     * @param resourcesAsString resource string
     * @return an array we can use on the town/nation screen
     */
    public static String[] formatResourcesStringForGovernmentScreenDisplay(String resourcesAsString) {
        if(resourcesAsString.isEmpty()) {
            return new String[0];
        } else {
            String[] resourcesAsFormattedArray = convertResourceAmountsStringToFormattedArray(resourcesAsString);
            if(resourcesAsFormattedArray.length > 20) {
                resourcesAsFormattedArray = Arrays.copyOf(resourcesAsFormattedArray, 21);
                resourcesAsFormattedArray[20] = "...";
            }
            return resourcesAsFormattedArray;
        }
    }
    
    /**
     *  Format resource string to something we can send to the dynmap
     *
     * @param resourcesAsString resource string
     * @return a string we can show on the dynmap e.g. "64 Wheat, 32 Iron"
     */
    public static String formatProductionStringForDynmapTownyDisplay(String resourcesAsString) {    
        if(resourcesAsString.isEmpty()) {
            return "";
        } else {
            String[] resourcesAsFormattedArray = convertResourceAmountsStringToFormattedArray(resourcesAsString);
            return Arrays.toString(resourcesAsFormattedArray).replace("[","").replace("]","");
        }
    }

    /**
     * Convert a resource amount string formatted array
     * 
     * NOTE: Do not pass in an empty string
     * 
     * @param resourcesAmountsString e.g. "64-WHEAT,64-COAL"
     * @return e.g. ["64 Wheat","64 Coal"]
     */
    private static String[] convertResourceAmountsStringToFormattedArray(String resourcesAmountsString) {
        //Return translated materials array
        List<String> resourcesAsFormattedList = new ArrayList<>();
        String[] resourcesAsArray = resourcesAmountsString.split(",");
        String[] amountAndMaterialName;
        String amount;
        String materialName;
        String translatedMaterialName;
        for(String resourceAsString: resourcesAsArray) {
            amountAndMaterialName = resourceAsString.split("-");
            amount = amountAndMaterialName[0];
            materialName = amountAndMaterialName[1];
            translatedMaterialName = formatMaterialNameForDisplay(materialName);
            resourcesAsFormattedList.add(amount + " " + translatedMaterialName);
        }
        return resourcesAsFormattedList.toArray(new String[0]);
    }
    
    public static String formatExtractionCategoryNameForDisplay(ResourceExtractionCategory resourceExtractionCategory) {
        String categoryName = resourceExtractionCategory.getName();
        if(TownyResourcesTranslation.hasKey("resource_category_" + categoryName)) {
            return TownyResourcesTranslation.of("resource_category_" + categoryName).split(",")[0];
        } else {
            return formatMaterialNameForDisplay(categoryName);
        }
    }

    public static String formatOfferCategoryNameForDisplay(ResourceOfferCategory resourceOfferCategory) {
        String categoryName = resourceOfferCategory.getName();
        if(TownyResourcesTranslation.hasKey("resource_category_"+ categoryName)) {
            return TownyResourcesTranslation.of("resource_category_" + categoryName).split(",")[1].trim();
        } else {
            return formatMaterialNameForDisplay(categoryName);
        }
    }
        
    public static String formatMaterialNameForDisplay(String materialName) {
        Material material = Material.getMaterial(materialName);
        if(material == null) {
            if(TownyResources.getPlugin().isSlimeFunInstalled()) {
                SlimefunItem slimefunItem = SlimefunItem.getById(materialName);
                if(slimefunItem != null) {
                    return slimefunItem.getItemName().replaceAll("[^\\w\\s]\\w","");
                }                
            }

            // mythicmobs integration
            if(TownyResources.getPlugin().isMythicMobsInstalled()) {
                Optional<MythicItem> maybeMythicItem = TownyResources.getPlugin().getMythicItemManager().getItem(materialName);

                if(maybeMythicItem.isPresent()) {
                    MythicItem mythicItem = maybeMythicItem.get();
                    String maybeDisplayName = mythicItem.getDisplayName();
                    if (maybeDisplayName != null)
                        return maybeDisplayName.replaceAll("[^\\w\\s]\\w","");
                }
            }

            // mmoitems integration
            if(TownyResources.getPlugin().isMMOItemsInstalled()) {
                // format: 'TYPE:MATERIALNAME'
                String[] parts = materialName.split(":");
                if (parts.length == 2 && MMOItems.plugin.getTypes().has(parts[0])) {
                    MMOItem mmoItem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(parts[0]), parts[1]);
                    if (mmoItem != null) {
                        if (mmoItem.hasData(ItemStats.NAME)) {
                            return MythicLib.plugin.parseColors(((NameData)mmoItem.getData(ItemStats.NAME)).bake());  // Known material
                        } else {
                            // Unknown, only return the part of the material name after the :
                            return WordUtils.capitalizeFully(parts[1].replaceAll("_", " "));
                        }
                    }
                }
            }
        } else {
            if(TownyResources.getPlugin().isLanguageUtilsInstalled()) {           
                ItemStack fakeItemStack = new ItemStack(material);
                String translatedMaterialName = LanguageHelper.getItemDisplayName(fakeItemStack, TownyResourcesSettings.getMaterialsDisplayLanguage());
                return translatedMaterialName;
            }
        }
        //Couldn't find a translation. Return un-translated material name
        return WordUtils.capitalizeFully(materialName.replaceAll("_", " "));
    }
}
