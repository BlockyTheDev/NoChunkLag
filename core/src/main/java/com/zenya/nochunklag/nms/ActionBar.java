package com.zenya.nochunklag.nms;

import org.bukkit.entity.Player;

public interface ActionBar {
    void send(Player player, String text);
    void sendToAll(String text);
}
