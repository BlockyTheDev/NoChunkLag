package com.zenya.nochunklag.util;

import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.nms.ActionBar;
import com.zenya.nochunklag.scheduler.TrackTPSTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatBuilder {
    private static final CooldownManager CDM = CooldownManager.getInstance();
    private static final ActionBar ACTION_BAR = NoChunkLag.getProtocolNMS().getActionBar();

    private String text;
    private Player player;
    private CommandSender sender;
    private World world;

    public ChatBuilder() {

    }

    public ChatBuilder(String text) {
        this.text = text;
    }

    public ChatBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public ChatBuilder withPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ChatBuilder withPlayer(String player) {
        this.player = Bukkit.getPlayer(player);
        return this;
    }

    public ChatBuilder withSender(CommandSender sender) {
        this.sender = sender;
        return this;
    }

    public ChatBuilder withWorld(String world) {
        this.world = Bukkit.getWorld(world);
        return this;
    }

    public ChatBuilder withWorld(World world) {
        this.world = world;
        return this;
    }

    public String build() {
        if(sender != null) {
            try {
                this.player = (Player) sender;
            } catch(ClassCastException exc) {
                player = null;
            }
        }

        //Placeholders
        text = text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
        text = text.replaceAll("%tps%", Float.toString(TrackTPSTask.getInstance().getAverageTps()));
        text = world == null ? text : text.replaceAll("%world%", world.getName());
        text = player == null ? text : text.replaceAll("%player%", player.getName());
        text = player == null ? text : text.replaceAll("%cooldown_elytra%", CDM.getTimer(CooldownType.ELYTRA_BOOST).getCooldown(player).toString());
        text = player == null ? text : text.replaceAll("%cooldown_trident%", CDM.getTimer(CooldownType.TRIDENT_RIPTIDE).getCooldown(player).toString());

        return text;
    }

    public void sendMessage(CommandSender sender) {
        this.sender = sender;
        sender.sendMessage(build());
    }

    public void sendMessage(Player player) {
        this.player = player;
        player.sendMessage(build());
    }

    public void sendMessage() {
        if(sender != null) {
            sendMessage(sender);
        } else if(player != null) {
            sendMessage(player);
        }
    }

    public void sendActionBar(Player player) {
        this.player = player;
        sendActionBar();
    }

    public void sendActionBar() {
        if(player != null) {
            ACTION_BAR.send(player, build());
        }
    }
}
