package com.zenya.nochunklag.nms.v1_17_R1;

import com.zenya.nochunklag.nms.ActionBar;
import com.zenya.nochunklag.nms.ProtocolNMS;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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
            packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), ChatMessageType.c, player.getUniqueId());
            ((CraftPlayer) player).getHandle().b.sendPacket(packet);
        }

        @Override
        public void sendToAll(String text) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), ChatMessageType.c, player.getUniqueId());
                ((CraftPlayer) player).getHandle().b.sendPacket(packet);
            }
        }
    }
}