package com.zenya.nochunklag;

import com.zenya.nochunklag.command.NoChunkLagCommand;
import com.zenya.nochunklag.command.NoChunkLagTab;
import com.zenya.nochunklag.event.LegacyListeners;
import com.zenya.nochunklag.event.ModernListeners;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.scheduler.TaskManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NoChunkLag extends JavaPlugin {

  private static NoChunkLag instance;

  @Override
  public void onEnable() {
    instance = this;

    //Register all runnables
    TaskManager.getInstance();

    //Init config and messages
    ConfigManager.getInstance();
    MessagesManager.getInstance();

    //Register events
    this.getServer().getPluginManager().registerEvents(new LegacyListeners(), this);
    //PlayerRiptideEvent only available post-aquatic update (>=1.13)
    this.getServer().getPluginManager().registerEvents(new ModernListeners(), this);

    //Register commands
    this.getCommand("nochunklag").setExecutor(new NoChunkLagCommand());
    this.getCommand("nochunklag").setTabCompleter(new NoChunkLagTab());
  }

  @Override
  public void onDisable() {

  }

  public static NoChunkLag getInstance() {
    return instance;
  }
}
