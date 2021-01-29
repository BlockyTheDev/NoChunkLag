package com.zenya.nochunklag.util;


import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.nms.ActionBar;
import com.zenya.nochunklag.scheduler.TrackTPSTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatUtils {
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
        message = message.replaceAll("%tps%", Float.toString(TrackTPSTask.getInstance().getAverageTps()));
        return message;
    }

    private static String parseSimpleMessage(String message) {
        message = translateColor(message);
        message = message.replaceAll("%tps%", Float.toString(TrackTPSTask.getInstance().getAverageTps()));
        return message;
    }

    public static void sendMessage(Player player, String message) {
        if(message == "") return;

        message = parseMessage(player, message);
        player.sendMessage(message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if(message == "") return;

        message = parseSimpleMessage(message);
        sender.sendMessage(message);
    }

    public static void sendBroadcast(String message) {
        if(message == "") return;

        message = parseSimpleMessage(message);

        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static void sendProtectedBroadcast(List<String> permissions, String message) {
        if(message == "") return;

        message = parseSimpleMessage(message);

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
        if(title == "") return;

        title = parseMessage(player, title);
        player.resetTitle();
        player.sendTitle(title, "");
    }

    public static void sendSubtitle(Player player, String subtitle) {
        if(subtitle == "") return;

        subtitle = parseMessage(player, subtitle);
        player.resetTitle();
        player.sendTitle("", subtitle);
    }

    public static void sendActionBar(Player player, String text) {
        if(text == "") return;

        text = parseMessage(player, text);
        actionBar.send(player, text);
    }

    public static void sendActionBarToAll(String text) {
        if(text == "") return;

        text = parseSimpleMessage(text);
        actionBar.sendToAll(text);
    }
}

