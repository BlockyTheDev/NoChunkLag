package com.zenya.nochunklag.event;

import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.scheduler.TrackCooldownTask;
import com.zenya.nochunklag.scheduler.TrackTPSTask;
import com.zenya.nochunklag.util.ChatBuilder;
import com.zenya.nochunklag.util.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import org.bukkit.Material;

public class ModernListeners implements Listener {

    private static NoChunkLag noChunkLag = NoChunkLag.getInstance();

    @EventHandler
    public void onPlayerRiptideEvent(PlayerRiptideEvent e) {
        Bukkit.getServer().getPluginManager().callEvent(new TridentRiptideEvent(noChunkLag.getCooldownManager(), e));
    }

    @EventHandler
    public void onTridentRiptideEvent(TridentRiptideEvent e) {
        Player player = e.getPlayer();
        ChatBuilder chat = new ChatBuilder(noChunkLag.getCooldownManager()).withPlayer(player).withWorld(player.getWorld());
        PermissionManager pm = new PermissionManager(player, CooldownType.TRIDENT_RIPTIDE);

        //Disable limits in disabled worlds
        if (e.isDisabledInWorld()) {
            return;
        }

        //Block boosting in disallowed worlds
        if (e.isDisallowedInWorld()) {
            e.setCancelled(true);
            chat.withText(MessagesManager.getInstance().getString("disallowed-in-world")).sendActionBar();;
            return;
        }

        if (e.isCanBoost()) {

            //Enforce cooldown
            e.setCooldown(pm.getGroupCooldown());
            new TrackCooldownTask(e);

            //Enforce speed multiplier
            if (pm.getGroupSpeedMultiplier() != 0) {
                int period = 1;
                new BukkitRunnable() {
                    int duration = (int) (e.getTrident().getEnchantmentLevel(Enchantment.RIPTIDE) * 0.25 * (20 / period));
                    int i = 0;

                    @Override
                    public void run() {
                        if (player.isRiptiding()) {
                            player.setVelocity(player.getLocation().getDirection().normalize().multiply(pm.getGroupSpeedMultiplier() + i / 50));
                            i++;
                            if (i >= duration) {
                                this.cancel();
                            }
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

            if (trident.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                Random rObj = new Random();
                float lossChance = (100 / ((trident.getEnchantmentLevel(Enchantment.DURABILITY) + 1)));
                duraLoss = 0;
                for (int i = 0; i < defaultDuraLoss; i++) {
                    float rFloat = rObj.nextFloat() * 100;
                    if (rFloat < lossChance) {
                        duraLoss += 1;
                    }
                }
            }

            if (!player.getGameMode().equals(GameMode.CREATIVE) && !trident.getItemMeta().isUnbreakable()) {
                if (trident.getDurability() + duraLoss >= trident.getType().getMaxDurability()) {
                    trident.setDurability(trident.getType().getMaxDurability());
                } else {
                    trident.setDurability((short) (trident.getDurability() + duraLoss));
                }
                if (player.getInventory().getItemInMainHand().getType() == Material.TRIDENT) {
                    player.getInventory().setItemInMainHand(trident);
                } else {
                    player.getInventory().setItemInOffHand(trident);
                }
            }

        } else {
            //Cannot boost, error messages
            e.setCancelled(true);

            //On cooldown
            if (e.getCooldown() > 0) {
                chat.withText(MessagesManager.getInstance().getString("cooldowns.trident-riptide")).sendActionBar();
                return;
            }

            //TPS below threshold
            if (TrackTPSTask.getInstance().getAverageTps() < ConfigManager.getInstance().getInt("noboost-tps-treshold")) {
                chat.withText(MessagesManager.getInstance().getString("low-tps")).sendActionBar();
                return;
            }

            //No permission for elytra+riptide
            chat.withText(MessagesManager.getInstance().getString("no-elytra-riptide")).sendActionBar();
        }
    }
}
