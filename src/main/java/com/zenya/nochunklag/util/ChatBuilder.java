package com.zenya.nochunklag.util;

import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.scheduler.TrackTPSTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatBuilder {

  private static final CooldownManager CDM = CooldownManager.getInstance();
//  private static final ActionBar ACTION_BAR = NoChunkLag.getProtocolNMS().getActionBar();

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
    if (sender != null) {
      try {
        this.player = (Player) sender;
      } catch (ClassCastException exc) {
        player = null;
      }
    }

    //Placeholders
    text = text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
    text = text.replaceAll("%tps%", Float.toString(TrackTPSTask.getInstance().getAverageTps()));
    text = world == null ? text : text.replaceAll("%world%", world.getName());
    text = player == null ? text : text.replaceAll("%player%", player.getName());
    text = player == null ? text : text.replaceAll("%cooldown_elytra%", CDM.getTimer(CooldownType.ELYTRA_BOOST).getCooldown(player).toString());
    text = player == null ? text : text.replaceAll("%elytra_bar%", timeToBars(player, CooldownType.ELYTRA_BOOST));
    text = player == null ? text : text.replaceAll("%trident_bar%", timeToBars(player, CooldownType.TRIDENT_RIPTIDE));
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
    if (sender != null) {
      sendMessage(sender);
    } else if (player != null) {
      sendMessage(player);
    }
  }

  public void sendActionBar(Player player) {
    this.player = player;
    sendActionBar();
  }

  public void sendActionBar() {
    if (player != null) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(build()));
    }
  }

  public static String timeToBars(Player player, CooldownType type) {
    PermissionManager pm = new PermissionManager(player, type);
    int totalDuration = pm.getGroupCooldown();
    if (totalDuration == 0) {
      return ChatColor.GREEN + "||||||||||";
    }
    int timeLeft = CDM.getTimer(type).getCooldown(player);
    int redProgress = Math.round((float) (10 * timeLeft / totalDuration)); //counting from the back
    int greenProgress = 10 - redProgress;

    String bar = "";
    bar += ChatColor.GREEN;
    for (int i = 0; i < greenProgress; i++) {
      bar += "|";
    }
    bar += ChatColor.RED;
    for (int i = 0; i < redProgress; i++) {
      bar += "|";
    }
    bar += ChatColor.RESET;

    return bar;
  }
}
