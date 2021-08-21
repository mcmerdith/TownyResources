package io.github.townyadvanced.townyresources.listeners;

import io.github.townyadvanced.townyresources.TownyResources;
import io.github.townyadvanced.townyresources.controllers.PlayerExtractionLimitsController;
import io.github.townyadvanced.townyresources.settings.TownyResourcesSettings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * 
 * @author Goosius
 *
 */
public class TownyResourcesBukkitEventListener implements Listener {

	@SuppressWarnings("unused")
	private final TownyResources plugin;
	
	public TownyResourcesBukkitEventListener(TownyResources instance) {
		plugin = instance;
	}

	@EventHandler()
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if(TownyResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processEntityDamageByEntityEvent(event);
		}
	}

	@EventHandler()
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if(TownyResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processEntityDeathEvent(event);
		}
	}

	@EventHandler()
	public void onBlockBreak(BlockBreakEvent event) {
		if(TownyResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processBlockBreakEvent(event);
		}	
	}
	
	@EventHandler()
	public void onBlockShearEntityEvent(BlockShearEntityEvent event) {
		if(TownyResourcesSettings.isEnabled()) {
			//Dispensers cannot shear entities
			event.setCancelled(true);
		}	
	}
	
	@EventHandler()
	public void onPlayerShearEntityEvent(PlayerShearEntityEvent event) {
		if(TownyResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processPlayerShearEntityEvent(event);
		}	
	}
	
	
	@EventHandler()
	public void onPlayerShearEntityEvent(EntityDropItemEvent event) {
		if(TownyResourcesSettings.isEnabled()) {
			//PlayerExtractionLimitsController.processEntityDropItemEvent(event);
		}	
	}
	
}
