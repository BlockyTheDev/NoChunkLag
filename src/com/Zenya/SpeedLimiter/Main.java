package com.Zenya.SpeedLimiter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(new Listeners(this), this);
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}

}
