package com.Zenya.SpeedLimiter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Listeners implements Listener {
	
	public final CooldownManager cooldownManager = new CooldownManager();
	private ConfigManager configManager = ConfigManager.getInstance();
	private final Plugin plugin;
	
	public Listeners(Plugin plugin) {
		this.plugin  = plugin;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		PlayerInventory inv = e.getPlayer().getInventory();
		int timeLeft = cooldownManager.getCooldown(e.getPlayer());
		
		if(e.getPlayer().hasPermission("speedlimiter.bypass") || e.getPlayer().hasPermission("speedlimit.bypass")) return;
		
		if(e.getAction() == Action.RIGHT_CLICK_AIR) {

			if(inv.getItemInMainHand().getType() == Material.FIREWORK_ROCKET || inv.getItemInOffHand().getType() == Material.FIREWORK_ROCKET) {
				if(inv.getArmorContents()[2].getType() == Material.ELYTRA) {
					if(timeLeft == 0) {
						cooldownManager.setCooldown(e.getPlayer(), configManager.getCooldown("elytra"));
						new BukkitRunnable() {
							@Override
							public void run() {
								int timeLeft = cooldownManager.getCooldown(e.getPlayer());
								cooldownManager.setCooldown(e.getPlayer(), --timeLeft);
								if(timeLeft == 0) {
									cancel();
								}
							}
						}.runTaskTimer(this.plugin, 20, 20);
					} else {
						String message = configManager.getMessage("elytra", cooldownManager.getCooldown(e.getPlayer()));
						e.getPlayer().sendTitle("", message, 10, 70, 20);
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerRiptideEvent(PlayerRiptideEvent e) {
		int timeLeft = cooldownManager.getCooldown(e.getPlayer());
		if(e.getPlayer().hasPermission("speedlimiter.bypass") || e.getPlayer().hasPermission("speedlimit.bypass")) return;
			if(timeLeft == 0) {
				cooldownManager.setCooldown(e.getPlayer(), configManager.getCooldown("trident"));
				new BukkitRunnable() {
					@Override
					public void run() {
						int timeLeft = cooldownManager.getCooldown(e.getPlayer());
						cooldownManager.setCooldown(e.getPlayer(), --timeLeft);
						if(timeLeft == 0) {
							cancel();
						}
					}
				}.runTaskTimer(this.plugin, 20, 20);
			} else {
				String message = configManager.getMessage("trident", cooldownManager.getCooldown(e.getPlayer()));
				e.getPlayer().sendTitle("", message, 10, 70, 20);

				Location oldLoc = e.getPlayer().getLocation();
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getPlayer().teleport(oldLoc);
					}
				}.runTaskLater(this.plugin, 1);
			}
		}
	
	@EventHandler
	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent e) {
		if(e.getPlayer().hasPermission("speedlimiter.bypass") || e.getPlayer().hasPermission("speedlimit.bypass")) return;

		if(e.getPlayer().isFlying()) return;
		if(e.getPlayer().isSprinting()) e.getPlayer().setSprinting(false);
	}
}
