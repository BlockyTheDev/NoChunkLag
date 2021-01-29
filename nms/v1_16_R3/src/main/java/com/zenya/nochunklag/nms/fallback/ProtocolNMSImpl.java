package com.zenya.nochunklag.nms.fallback;

import com.zenya.nochunklag.nms.ActionBar;
import com.zenya.nochunklag.nms.ProtocolNMS;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ProtocolNMSImpl implements ProtocolNMS {
    @Override
    public ActionBar getActionBar() {
        return new ActionBarImpl();
    }

    public class ActionBarImpl implements ActionBar {
        private PacketPlayOutChat packet;

        @Override
        public void send(Player player, String text) {
            packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), ChatMessageType.GAME_INFO, player.getUniqueId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        @Override
        public void sendToAll(String text) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), ChatMessageType.GAME_INFO, player.getUniqueId());
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }
}