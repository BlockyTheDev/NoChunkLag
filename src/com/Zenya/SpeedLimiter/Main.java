package com.Zenya.SpeedLimiter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Plugin instance;
	public void onEnable() {
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(new Listeners(this), this);
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}

}
