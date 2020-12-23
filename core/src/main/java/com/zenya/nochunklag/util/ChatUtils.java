package com.zenya.nochunklag.util;


import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.nms.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatUtils {
    private static NoChunkLag noChunkLag = NoChunkLag.getInstance();
    private static CooldownManager cdm = CooldownManager.getInstance();
    private static ActionBar actionBar = NoChunkLag.getProtocolNMS().getActionBar();

    private static String translateColor(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    private static String parseMessage(Player player, String message) {
        message = translateColor(message);
        message = message.replaceAll("%cooldown_elytra%", cdm.getTimer(CooldownType.ELYTRA_BOOST).getCooldown(player).toString());
        message = message.replaceAll("%cooldown_trident%", cdm.getTimer(CooldownType.TRIDENT_RIPTIDE).getCooldown(player).toString());
        message = message.replaceAll("%tps%", Float.toString(noChunkLag.getTPSTracker().getTps()));
        return message;
    }

    public static void sendMessage(Player player, String message) {
        message = parseMessage(player, message);
        player.sendMessage(message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        message = translateColor(message);
        sender.sendMessage(message);
    }

    public static void sendBroadcast(String message) {
        message = translateColor(message);

        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static void sendProtectedBroadcast(List<String> permissions, String message) {
        message = translateColor(message);

        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            for(String permission : permissions) {
                if(player.hasPermission(permission)) {
                    player.sendMessage(message);
                    continue;
                }
            }
        }
    }

    public static void sendTitle(Player player, String title) {
        title = parseMessage(player, title);
        player.resetTitle();
        player.sendTitle(title, null, 10, 40, 20);
    }

    public static void sendSubtitle(Player player, String subtitle) {
        subtitle = parseMessage(player, subtitle);
        player.resetTitle();
        player.sendTitle(null, subtitle, 0, 10, 0);
    }

    public static void sendActionBar(Player player, String text) {
        text = parseMessage(player, text);
        actionBar.send(player, text);
    }

    public static void sendActionBarToAll(String text) {
        text = translateColor(text);
        actionBar.sendToAll(text);
    }
}

