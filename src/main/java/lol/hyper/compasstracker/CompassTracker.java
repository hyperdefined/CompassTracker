package lol.hyper.compasstracker;

import lol.hyper.compasstracker.commands.CommandSetTracker;
import lol.hyper.compasstracker.events.CompassEvent;
import lol.hyper.compasstracker.events.PlayerLeaveEvent;
import lol.hyper.compasstracker.events.PlayerRespawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class CompassTracker extends JavaPlugin {

    public Player speedrunner;
    private static CompassTracker instance;
    public Location location;
    public boolean gameStarted = false;
    public ArrayList<Player> hunters = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new CompassEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLeaveEvent(), this);
        this.getCommand("ct").setExecutor(new CommandSetTracker());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CompassTracker getInstance() {
        return instance;
    }

}
