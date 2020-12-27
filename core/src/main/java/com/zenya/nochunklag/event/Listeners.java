package com.zenya.nochunklag.event;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.scheduler.TrackTPSTask;
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
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Random;

public class Listeners implements Listener {
    private static NoChunkLag noChunkLag = NoChunkLag.getInstance();
    private static ConfigManager configManager = ConfigManager.getInstance();
    private static MessagesManager messagesManager = MessagesManager.getInstance();
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
    public void onPlayerRiptideEvent(PlayerRiptideEvent e) {
        Bukkit.getServer().getPluginManager().callEvent(new TridentRiptideEvent(e));
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
                ChatUtils.sendActionBar(player, messagesManager.getString("cooldowns.elytra-boost"));
                return;
            }

            //TPS below threshold
            ChatUtils.sendActionBar(player, messagesManager.getString("low-tps"));
            return;
        }
    }

    @EventHandler
    public void onTridentRiptideEvent(TridentRiptideEvent e) {
        Player player = e.getPlayer();
        PermissionManager pm = new PermissionManager(player, CooldownType.TRIDENT_RIPTIDE);

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
                        metaUtils.clearMeta(player, "nochunklag.notified.tridentready");
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
                    int duration = (int) (e.getTrident().getEnchantmentLevel(XEnchantment.RIPTIDE.parseEnchantment()) * 0.25 * (20 / period));
                    int i = 0;
                    @Override
                    public void run() {
                        if(player.isRiptiding()) {
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
            ItemStack trident = e.getTrident();
            int defaultDuraLoss = pm.getGroupDurabilityLoss();
            int duraLoss = defaultDuraLoss;

            if(trident.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                Random rObj = new Random();
                float lossChance = (100 / ((trident.getEnchantmentLevel(Enchantment.DURABILITY)+1)));
                duraLoss = 0;
                for(int i=0; i<defaultDuraLoss; i++) {
                    float rFloat = rObj.nextFloat() * 100;
                    if(rFloat < lossChance) {
                        duraLoss += 1;
                    }
                }
            }

            if(!player.getGameMode().equals(GameMode.CREATIVE) && !trident.getItemMeta().isUnbreakable()) {
                if(trident.getDurability() + duraLoss >= trident.getType().getMaxDurability()) {
                    trident.setType(XMaterial.AIR.parseMaterial());
                } else {
                    trident.setDurability((short) (trident.getDurability() + duraLoss));
                }

                if(player.getInventory().getItemInMainHand().getType().equals(XMaterial.TRIDENT.parseMaterial())) {
                    player.getInventory().setItemInMainHand(trident);
                } else {
                    player.getInventory().setItemInOffHand(trident);
                }
            }

        } else {
            //Cannot boost, error messages
            e.setCancelled(true);

            //On cooldown
            if(e.getCooldown() > 0) {
                ChatUtils.sendActionBar(player, messagesManager.getString("cooldowns.trident-riptide"));
                return;
            }

            //TPS below threshold
            if(TrackTPSTask.getInstance().getTps() < configManager.getInt("noboost-tps-treshold")) {
                ChatUtils.sendActionBar(player, messagesManager.getString("low-tps"));
                return;
            }

            //No permission for elytra+riptide
            ChatUtils.sendActionBar(player, messagesManager.getString("no-elytra-riptide"));
            return;
        }
    }
}
