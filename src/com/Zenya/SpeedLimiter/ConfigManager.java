package com.Zenya.SpeedLimiter;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import sun.security.krb5.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigManager {
	
	Plugin plugin;
	FileConfiguration config;
	private static ConfigManager configManager;
	
	public ConfigManager() {
		this.plugin = Main.instance;
		this.config = plugin.getConfig();
		
		if(!(this.getConfigExists())) {
			this.setDefaults();
		}
	}
	
	public void setDeveloperDefaults() { // For config revisions
		plugin.saveDefaultConfig();
		config.set("config-version", 1);
		
		for(EntityType type: EntityType.values()) {
			if(type.isAlive()) {
				String name = type.toString().toUpperCase();
				config.set("mobs."+name+".name", "&c"+name.toUpperCase()+" Spawner");
				config.set("mobs."+name+".drop-chance", 1);
			}
		}
		plugin.saveConfig();
	}
	
	public void setDefaults() {
		plugin.saveDefaultConfig();
	}
	
	public boolean getConfigExists() {
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		return configFile.exists();
	}

	public int getCooldown(String type) {
		return config.getInt(type.toLowerCase() + "-cooldown");
	}
	
	public static ConfigManager getInstance() {
		if(configManager == null) {
			configManager = new ConfigManager();
		}
		return configManager;
	}

}
