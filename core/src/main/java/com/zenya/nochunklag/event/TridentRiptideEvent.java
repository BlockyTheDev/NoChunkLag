package com.zenya.nochunklag.event;

import com.cryptomorin.xseries.XMaterial;
import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.scheduler.TrackTPSTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TridentRiptideEvent extends Event implements Cancellable {
    private static NoChunkLag noChunkLag = NoChunkLag.getInstance();
    private static CooldownManager cdm = CooldownManager.getInstance();

    private PlayerRiptideEvent playerRiptideEvent;
    private boolean isCancelled;

    public TridentRiptideEvent(PlayerRiptideEvent e) {
        this.playerRiptideEvent = e;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return playerRiptideEvent.getPlayer();
    }

    public ItemStack getTrident() {
        if(getPlayer().getInventory().getItemInMainHand().getType().equals(XMaterial.TRIDENT.parseMaterial())) {
            return getPlayer().getInventory().getItemInMainHand();
        }
        return getPlayer().getInventory().getItemInOffHand();
    }

    public int getCooldown() {
        return cdm.getTimer(CooldownType.TRIDENT_RIPTIDE).getCooldown(getPlayer());
    }

    public void setCooldown(int time) {
        cdm.getTimer(CooldownType.TRIDENT_RIPTIDE).setCooldown(getPlayer(), time);
    }

    public boolean isDisabledInWorld() {
        //In bypass world
        if(ConfigManager.getInstance().getList("disabled-worlds") != null && ConfigManager.getInstance().getList("disabled-worlds").size() != 0) {
            for (String worldname : ConfigManager.getInstance().getList("disabled-worlds")) {
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
                //Not wearing elytra or has bypass permission
                try {
                    if(!getPlayer().getInventory().getChestplate().getType().equals(XMaterial.ELYTRA.parseMaterial()) || getPlayer().hasPermission(ConfigManager.getInstance().getString("elytra-riptide-permission"))) {
                        return true;
                    }
                } catch (NullPointerException ex) {
                    return true;
                    //Silence errors
                }
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
            Location oldLoc = getPlayer().getLocation();
            new BukkitRunnable() {
                public void run() {
                    getPlayer().teleport(oldLoc);
                }
            }.runTaskLater(noChunkLag, 1);
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
