package com.Zenya.SpeedLimiter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners implements Listener {
	
	public final CooldownManager cooldownManager = new CooldownManager();
	private final Plugin plugin;
	
	public Listeners(Plugin plugin) {
		this.plugin  = plugin;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		PlayerInventory inv = e.getPlayer().getInventory();
		int timeLeft = cooldownManager.getCooldown(e.getPlayer());
		
		if(e.getPlayer().hasPermission("speedlimiter.bypass") || e.getPlayer().hasPermission("speedlimit.bypass")) return;
		
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if(inv.getItemInMainHand().getType() == Material.FIREWORK_ROCKET || inv.getItemInOffHand().getType() == Material.FIREWORK_ROCKET) {
				if(inv.getArmorContents()[2].getType() == Material.ELYTRA) {
					if(timeLeft == 0) {
						cooldownManager.setCooldown(e.getPlayer(), 15);
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
						String message = ChatColor.RED + "Wait " + ChatColor.DARK_RED + timeLeft + ChatColor.RED + " seconds before using this again";
						e.getPlayer().sendTitle("", message, 10, 70, 20);
						e.setCancelled(true);
					}
				}
			}
			
			else if((inv.getItemInMainHand().getType() == Material.TRIDENT && inv.getItemInMainHand().containsEnchantment(Enchantment.RIPTIDE)) || (inv.getItemInOffHand().getType() == Material.TRIDENT && inv.getItemInOffHand().containsEnchantment(Enchantment.RIPTIDE))) {
				if(timeLeft == 0) {
					cooldownManager.setCooldown(e.getPlayer(), 5);
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
					String message = ChatColor.RED + "Wait " + ChatColor.DARK_RED + timeLeft + ChatColor.RED + " seconds before using this again";
					e.getPlayer().sendTitle("", message, 10, 70, 20);
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent e) {
		if(e.getPlayer().isFlying()) return;
		if(e.getPlayer().isSprinting()) e.getPlayer().setSprinting(false);
	}
}
