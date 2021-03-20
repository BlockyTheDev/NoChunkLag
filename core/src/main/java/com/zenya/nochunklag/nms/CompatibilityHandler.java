package com.zenya.nochunklag.nms;

import com.zenya.nochunklag.util.ChatBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CompatibilityHandler {
    private static final String PACKAGE_DOMAIN = "com.zenya.nochunklag.nms.";
    private static final String CLASS_NAME = ".ProtocolNMSImpl";

    public static String getVersion() {
        /*
        1.9.2 - v1_9_R1
        1.9.4 - v1_9_R2
        1.10.2 - v1_10_R1
        1.11.2 - v1_11_R1
        1.12.2 - v1_12_R1
        1.13 - v1_13_R1
        1.13.2 - v1_13_R2
        1.14.4 - v1_14_R1
        1.15.2 - v1_15_R1
        1.16.1 - v1_16_R1
        1.16.3 - v1_16_R2
        1.16.5 - v1_16_R3
        */
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);
        return version;
    }

    public static int getProtocol() {
        return Integer.parseInt(getVersion().split("_")[1]);
    }

    public static Class<? extends ProtocolNMS> getProtocolNMS() throws ClassNotFoundException {
        try {
            return (Class<? extends ProtocolNMS>) Class.forName(PACKAGE_DOMAIN + getVersion() + CLASS_NAME);
        } catch (ClassNotFoundException e) {
            ChatBuilder chat = new ChatBuilder().withSender(Bukkit.getConsoleSender());
            chat.withText("&4You are running NoChunkLag on an unsupported NMS version " + getVersion()).sendMessage();
            chat.withText("&4Some features like actionbar messages may be disabled or broken").sendMessage();
            return (Class<? extends ProtocolNMS>) Class.forName(PACKAGE_DOMAIN + "fallback" + CLASS_NAME);
        }
    }
}
