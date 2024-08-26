package me.lukasabbe.griefpreventionhome;

import me.lukasabbe.griefpreventionhome.commands.PlotsCommand;
import me.lukasabbe.griefpreventionhome.commands.TabCompletePlots;
import org.bukkit.plugin.java.JavaPlugin;

public final class GriefPreventionHome extends JavaPlugin {
    public static GriefPreventionHome instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        GriefPreventionHome.instance = this;
        final PlotsCommand listener = new PlotsCommand();
        this.getCommand("plots").setExecutor(listener);
        this.getCommand("plots").setTabCompleter(new TabCompletePlots());
        this.getServer().getPluginManager().registerEvents(listener,this);
    }
}
