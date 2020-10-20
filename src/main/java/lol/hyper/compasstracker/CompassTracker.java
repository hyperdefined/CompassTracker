package lol.hyper.compasstracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class CompassTracker extends JavaPlugin {

    public Player speedrunner;
    public Location location;
    public boolean gameStarted = false;
    public ArrayList<Player> hunters = new ArrayList<>();
    public int trackerTask = 0;
    public CommandCT commandCT;
    public Events events;

    @Override
    public void onEnable() {
        events = new Events(this);
        commandCT = new CommandCT(this);
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        this.getCommand("ct").setExecutor(new CommandCT(this));
        new UpdateChecker(this, 79938).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                this.getLogger().info("You are running the latest version.");
            } else {
                this.getLogger().info("There is a new version available! Please download at https://www.spigotmc.org/resources/compasstracker.79938/");
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void startGame() {
        gameStarted = true;
        for (Player player : hunters) {
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta meta = compass.getItemMeta();
            meta.setDisplayName("[Compass Tracker]");
            compass.setItemMeta(meta);
            player.getInventory().addItem(compass);
        }
        trackerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (gameStarted) {
                if (speedrunner.getWorld().getName().equals("world")) {
                    location = speedrunner.getLocation();
                }
            }
        }, 0L, 60);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.RED + speedrunner.getName() + " is now being tracked!");
        }
    }

    public void endGame() {
        gameStarted = false;
        speedrunner = null;
        for (Player hunters : hunters) {
            hunters.getInventory().remove(Material.COMPASS);
        }
        Bukkit.getScheduler().cancelTask(trackerTask);
    }

    public static void giveCompass(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("[Compass Tracker]");
        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);
    }
}
