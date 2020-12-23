package com.zenya.nochunklag.event;

import com.cryptomorin.xseries.XMaterial;
import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraBoostEvent extends Event implements Cancellable {
    private static NoChunkLag noChunkLag = NoChunkLag.getInstance();
    private static ConfigManager configManager = ConfigManager.getInstance();
    private static CooldownManager cdm = CooldownManager.getInstance();

    private PlayerInteractEvent playerInteractEvent;
    private boolean isCancelled;

    public ElytraBoostEvent(PlayerInteractEvent e) {
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
        if(getPlayer().getInventory().getItemInMainHand().getType().equals(XMaterial.FIREWORK_ROCKET.parseMaterial())) {
            return getPlayer().getInventory().getItemInMainHand();
        }
        if(getPlayer().getInventory().getItemInOffHand().getType().equals(XMaterial.FIREWORK_ROCKET.parseMaterial())) {
            return getPlayer().getInventory().getItemInOffHand();
        }
        return null;
    }

    public int getCooldown() {
        return cdm.getTimer(CooldownType.ELYTRA_BOOST).getCooldown(getPlayer());
    }

    public void setCooldown(int time) {
        cdm.getTimer(CooldownType.ELYTRA_BOOST).setCooldown(getPlayer(), time);
    }

    public boolean isDisabledInWorld() {
        //In bypass world
        if(configManager.getList("disabled-worlds") != null && configManager.getList("disabled-worlds").size() != 0) {
            for (String worldname : configManager.getList("disabled-worlds")) {
                if (getPlayer().getWorld().getName().equals(worldname)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCanBoost() {
        //Not on cooldown
        if(getCooldown() < 1) {
            //TPS above threshold
            if(noChunkLag.getTPSTracker().getTps() > configManager.getInt("noboost-tps-treshold")) {
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

        if(isCancelled()) {
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