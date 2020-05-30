package lol.hyper.compasstracker;

import lol.hyper.compasstracker.commands.CommandSetTracker;
import lol.hyper.compasstracker.commands.CommandGiveCompass;
import lol.hyper.compasstracker.events.CompassEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CompassTracker extends JavaPlugin {

    public Player speedrunner;
    private static CompassTracker instance;
    public Location location;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new CompassEvent(), this);
        this.getCommand("settracker").setExecutor(new CommandSetTracker());
        this.getCommand("givecompass").setExecutor(new CommandGiveCompass());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CompassTracker getInstance() {
        return instance;
    }

}
