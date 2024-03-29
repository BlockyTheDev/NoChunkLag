package com.zenya.nochunklag.event;

import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.scheduler.TrackTPSTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraBoostEvent extends Event implements Cancellable {

    private CooldownManager cooldownManager;
    private PlayerInteractEvent playerInteractEvent;
    private boolean isCancelled;

    public ElytraBoostEvent(CooldownManager cooldownManager, PlayerInteractEvent e) {
        this.cooldownManager = cooldownManager;
        this.playerInteractEvent = e;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return playerInteractEvent.getPlayer();
    }

    public ItemStack getElytra() {
        return getPlayer().getInventory().getChestplate();
    }

    public ItemStack getFirework() {
        ItemStack mainHandItem = getPlayer().getInventory().getItemInMainHand();
        if (mainHandItem.getType() == Material.FIREWORK_ROCKET) {
            return mainHandItem;
        }
        ItemStack offHandItem = getPlayer().getInventory().getItemInOffHand();
        if (offHandItem.getType() == Material.FIREWORK_ROCKET) {
            return offHandItem;
        }
        return null;
    }

    public int getCooldown() {
        return cooldownManager.getTimer(CooldownType.ELYTRA_BOOST).getCooldown(getPlayer());
    }

    public void setCooldown(int time) {
        cooldownManager.getTimer(CooldownType.ELYTRA_BOOST).setCooldown(getPlayer(), time);
    }

    public boolean isDisabledInWorld() {
        //In bypass world
        if (ConfigManager.getInstance().getList("disabled-worlds") != null && !ConfigManager.getInstance().getList("disabled-worlds").isEmpty()) {
            for (String worldname : ConfigManager.getInstance().getList("disabled-worlds")) {
                if (getPlayer().getWorld().getName().equals(worldname)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDisallowedInWorld() {
        //In bypass world
        if (ConfigManager.getInstance().getList("disallowed-worlds") != null && !ConfigManager.getInstance().getList("disallowed-worlds").isEmpty()) {
            for (String worldname : ConfigManager.getInstance().getList("disallowed-worlds")) {
                if (getPlayer().getWorld().getName().equals(worldname)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCanBoost() {
        //Not on cooldown
        if (getCooldown() < 1) {
            //TPS above threshold
            if (TrackTPSTask.getInstance().getAverageTps() > ConfigManager.getInstance().getInt("noboost-tps-treshold")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;

        if (isCancelled()) {
            playerInteractEvent.setCancelled(true);
        }
    }

    //Default custom event methods
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
