package com.zenya.nochunklag.event;

import com.cryptomorin.xseries.XMaterial;
import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.scheduler.TrackCooldownTask;
import com.zenya.nochunklag.util.ChatBuilder;
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

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MetaUtils.setMeta(e.getPlayer(), "nochunklag.notified.regtps", "");
                MetaUtils.setMeta(e.getPlayer(), "nochunklag.notified.elytraready", "");
                MetaUtils.setMeta(e.getPlayer(), "nochunklag.notified.tridentready", "");
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
        ChatBuilder chat = new ChatBuilder().withPlayer(player).withWorld(player.getWorld());
        PermissionManager pm = new PermissionManager(player, CooldownType.ELYTRA_BOOST);

        //Disable limits in disabled worlds
        if(e.isDisabledInWorld()) return;

        //Block boosting in disallowed worlds
        if(e.isDisallowedInWorld()) {
            e.setCancelled(true);
            chat.withText(MessagesManager.getInstance().getString("disallowed-in-world")).sendActionBar();
            return;
        }

        if(e.isCanBoost()) {

            //Enforce cooldown
            e.setCooldown(pm.getGroupCooldown());
            new TrackCooldownTask(e);

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
                    int saveElytra = ConfigManager.getInstance().getBool("break-elytra") ? 0 : -1;
                    elytra.setDurability((short) (elytra.getType().getMaxDurability() + saveElytra));
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
                chat.withText(MessagesManager.getInstance().getString("cooldowns.elytra-boost")).sendActionBar();
                return;
            }

            //TPS below threshold
            chat.withText(MessagesManager.getInstance().getString("low-tps")).sendActionBar();
            return;
        }
    }
}
