package com.zenya.nochunklag.event;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.scheduler.TrackCooldownTask;
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
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ModernListeners implements Listener {
    private static NoChunkLag noChunkLag = NoChunkLag.getInstance();
    private static MetaUtils metaUtils = MetaUtils.getInstance();

    @EventHandler
    public void onPlayerRiptideEvent(PlayerRiptideEvent e) {
        Bukkit.getServer().getPluginManager().callEvent(new TridentRiptideEvent(e));
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
            new TrackCooldownTask(e);

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
                ChatUtils.sendActionBar(player, MessagesManager.getInstance().getString("cooldowns.trident-riptide"));
                return;
            }

            //TPS below threshold
            if(TrackTPSTask.getInstance().getAverageTps() < ConfigManager.getInstance().getInt("noboost-tps-treshold")) {
                ChatUtils.sendActionBar(player, MessagesManager.getInstance().getString("low-tps"));
                return;
            }

            //No permission for elytra+riptide
            ChatUtils.sendActionBar(player, MessagesManager.getInstance().getString("no-elytra-riptide"));
            return;
        }
    }
}
