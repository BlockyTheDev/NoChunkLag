package com.zenya.nochunklag.event;

import com.cryptomorin.xseries.XMaterial;
import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.util.ChatUtils;
import com.zenya.nochunklag.util.MetaUtils;
import com.zenya.nochunklag.util.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class LegacyListeners implements Listener {
    private static NoChunkLag noChunkLag = NoChunkLag.getInstance();
    private static MetaUtils metaUtils = MetaUtils.getInstance();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                metaUtils.setMeta(e.getPlayer(), "nochunklag.notified.regtps", "");
                metaUtils.setMeta(e.getPlayer(), "nochunklag.notified.elytraready", "");
                metaUtils.setMeta(e.getPlayer(), "nochunklag.notified.tridentready", "");
            }
        }.runTaskAsynchronously(NoChunkLag.getInstance());
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();

        try {
            if (player.getInventory().getChestplate().getType().equals(XMaterial.ELYTRA.parseMaterial())) {
                if(e.getItem().getType().equals(XMaterial.FIREWORK_ROCKET.parseMaterial())) {
                    if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                        if(player.isGliding()) {
                            Bukkit.getServer().getPluginManager().callEvent(new ElytraBoostEvent(e));
                        }
                    }
                }
            }
        } catch(NullPointerException ex) {
            //Silence errors
        }
    }

    @EventHandler
    public void onElytraBoostEvent(ElytraBoostEvent e) {
        Player player = e.getPlayer();
        PermissionManager pm = new PermissionManager(player, CooldownType.ELYTRA_BOOST);

        //Disable limits in disabled worlds
        if(e.isDisabledInWorld()) return;

        if(e.isCanBoost()) {

            //Enforce cooldown
            e.setCooldown(pm.getGroupCooldown());
            new BukkitRunnable() {
                @Override
                public void run() {
                    int timeLeft = e.getCooldown();
                    e.setCooldown(--timeLeft);
                    if(timeLeft == 0) {
                        //Clear meta only if cooldown is applicable to player
                        metaUtils.clearMeta(player, "nochunklag.notified.elytraready");
                    }
                    if(timeLeft <= 0) {
                        //Cancel task regardless when it expires
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(NoChunkLag.getInstance(), 0, 20);

            //Enforce speed multiplier
            if(pm.getGroupSpeedMultiplier() != 0) {
                int period = 1;
                new BukkitRunnable() {
                    int duration = 2 * (20 / period);
                    int i = 0;
                    @Override
                    public void run() {
                        if(player.isGliding()) {
                            player.setVelocity(player.getLocation().getDirection().normalize().multiply(pm.getGroupSpeedMultiplier() + i/50));
                            i++;
                            if(i >= duration) this.cancel();
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(noChunkLag, 0, period);
            }

            //Enforce durability loss
            ItemStack elytra = e.getElytra();
            int defaultDuraLoss = pm.getGroupDurabilityLoss();
            int duraLoss = defaultDuraLoss;

            if(elytra.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                Random rObj = new Random();
                float lossChance = (100 / ((elytra.getEnchantmentLevel(Enchantment.DURABILITY)+1)));
                duraLoss = 0;
                for(int i=0; i<defaultDuraLoss; i++) {
                    float rFloat = rObj.nextFloat() * 100;
                    if(rFloat < lossChance) {
                        duraLoss += 1;
                    }
                }
            }
            if(!player.getGameMode().equals(GameMode.CREATIVE) && !elytra.getItemMeta().isUnbreakable()) {
                if(elytra.getDurability() + duraLoss >= elytra.getType().getMaxDurability()) {
                    elytra.setType(XMaterial.AIR.parseMaterial());
                } else {
                    elytra.setDurability((short) (elytra.getDurability() + duraLoss));
                }
                player.getInventory().setChestplate(elytra);
            }

        } else {
            //Cannot boost, error messages
            e.setCancelled(true);

            //On cooldown
            if(e.getCooldown() > 0) {
                ChatUtils.sendActionBar(player, MessagesManager.getInstance().getString("cooldowns.elytra-boost"));
                return;
            }

            //TPS below threshold
            ChatUtils.sendActionBar(player, MessagesManager.getInstance().getString("low-tps"));
            return;
        }
    }
}
