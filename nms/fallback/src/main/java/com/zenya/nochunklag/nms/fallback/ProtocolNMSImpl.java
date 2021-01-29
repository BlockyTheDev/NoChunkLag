package com.zenya.nochunklag.nms.fallback;

import com.zenya.nochunklag.nms.ActionBar;
import com.zenya.nochunklag.nms.ProtocolNMS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProtocolNMSImpl implements ProtocolNMS {
    @Override
    public ActionBar getActionBar() {
        return new ActionBarImpl();
    }

    public class ActionBarImpl implements ActionBar {
        @Override
        public void send(Player player, String text) {
            player.resetTitle();
            player.sendTitle("", text);
        }

        @Override
        public void sendToAll(String text) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.resetTitle();
                player.sendTitle("", text);
            }
        }
    }
}