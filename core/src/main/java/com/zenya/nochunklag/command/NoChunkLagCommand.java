package com.zenya.nochunklag.command;

import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NoChunkLagCommand implements CommandExecutor {

    private void sendUsage(CommandSender sender) {
        ChatUtils.sendMessage(sender, "&8&m*]----------[*&r &aNoChunkLag &8&m*]----------[*&r");
        ChatUtils.sendMessage(sender, "&a/nochunklag help&f -&2 Shows this help page");
        ChatUtils.sendMessage(sender, "&a/nochunklag reload&f -&2 Reloads the plugin\'s config and messages");
        ChatUtils.sendMessage(sender, "&8&m*]------------------------------------[*&r");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if(args.length != 1) {
            sendUsage(sender);
            return true;
        }

        if(!sender.hasPermission("nochunklag.command." + args[0])) {
            ChatUtils.sendMessage(sender, "&4You do not have permission to use this command");
            return true;
        }

        if(args[0].toLowerCase().equals("help")) {
            sendUsage(sender);
            return true;
        }
        if(args[0].toLowerCase().equals("reload")) {
            ConfigManager.reloadConfig();
            MessagesManager.reloadMessages();
            ChatUtils.sendMessage(sender, "&aNoChunkLag has been reloaded");
            return true;
        }
        sendUsage(sender);
        return true;
    }
}
