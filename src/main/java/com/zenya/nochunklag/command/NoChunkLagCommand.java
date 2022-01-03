package com.zenya.nochunklag.command;

import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.util.ChatBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NoChunkLagCommand implements CommandExecutor {

  private void sendUsage(CommandSender sender) {
    ChatBuilder chat = new ChatBuilder().withSender(sender);

    chat.withText("&8&m*]----------[*&r &aNoChunkLag &8&m*]----------[*&r").sendMessage();
    chat.withText("&a/nochunklag help&f -&2 Shows this help page").sendMessage();
    chat.withText("&a/nochunklag reload&f -&2 Reloads the plugin\'s config and messages").sendMessage();;
    chat.withText("&8&m*]------------------------------------[*&r").sendMessage();;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
    ChatBuilder chat = new ChatBuilder().withSender(sender);

    if (args.length != 1) {
      sendUsage(sender);
      return true;
    }

    if (!sender.hasPermission("nochunklag.command." + args[0])) {
      chat.withText("&4You do not have permission to use this command").sendMessage();
      return true;
    }

    if (args[0].toLowerCase().equals("help")) {
      sendUsage(sender);
      return true;
    }
    if (args[0].toLowerCase().equals("reload")) {
      ConfigManager.reloadConfig();
      MessagesManager.reloadMessages();
      chat.withText("&aNoChunkLag has been reloaded").sendMessage();
      return true;
    }
    sendUsage(sender);
    return true;
  }
}
