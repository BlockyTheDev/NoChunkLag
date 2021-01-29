package com.zenya.nochunklag.nms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CompatibilityHandler {
    private static CompatibilityHandler compatibilityHandler;
    private static final String PACKAGE_DOMAIN = "com.zenya.nochunklag.nms.";
    private static final String CLASS_NAME = ".ProtocolNMSImpl";

    public static String getVersion() {
        /*
        1.8 - v1_8_R3
        1.9 - v1_9_R2
        1.10 - v1_10_R1
        1.11 - v1_11_R1
        1.12 - v1_12_R1
        1.13 - v1_13_R2
        1.14 - v1_14_R1
        1.15 - v1_15_R1
        1.16 - v1_16_R3
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
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "You are running NoChunkLag on an unsupported NMS version " + getVersion());
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Some features like actionbar messages may be disabled or broken");
            return (Class<? extends ProtocolNMS>) Class.forName(PACKAGE_DOMAIN + "fallback" + CLASS_NAME);
        }
    }

    public static CompatibilityHandler getInstance() {
        if(compatibilityHandler == null) {
            compatibilityHandler = new CompatibilityHandler();
        }
        return compatibilityHandler;
    }
}
