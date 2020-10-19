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

    private static CompassTracker instance;
    public Player speedrunner;
    public Location location;
    public boolean gameStarted = false;
    public ArrayList<Player> hunters = new ArrayList<>();
    public int trackerTask = 0;

    public static CompassTracker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        this.getCommand("ct").setExecutor(new CommandCT());
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

    public static void startGame() {
        getInstance().gameStarted = true;
        for (Player player : getInstance().hunters) {
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta meta = compass.getItemMeta();
            meta.setDisplayName("[Compass Tracker]");
            compass.setItemMeta(meta);
            player.getInventory().addItem(compass);
        }
        getInstance().trackerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getInstance(), () -> {
            if (getInstance().gameStarted) {
                if (getInstance().speedrunner.getWorld().getName().equals("world")) {
                    getInstance().location = getInstance().speedrunner.getLocation();
                }
            }
        }, 0L, 60);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.RED + CompassTracker.getInstance().speedrunner.getName() + " is now being tracked!");
        }
    }

    public static void endGame() {
        getInstance().gameStarted = false;
        getInstance().speedrunner = null;
        for (Player hunters : getInstance().hunters) {
            hunters.getInventory().remove(Material.COMPASS);
        }
        Bukkit.getScheduler().cancelTask(getInstance().trackerTask);
    }

    public static void giveCompass(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("[Compass Tracker]");
        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);
    }
}
