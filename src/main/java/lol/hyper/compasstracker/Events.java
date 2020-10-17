package lol.hyper.compasstracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    @EventHandler
    public void onPlayerJoinLeave(org.bukkit.event.entity.EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            Player player = event.getEntity().getKiller();
            if (player == CompassTracker.getInstance().speedrunner) {
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won the game! They killed the Ender Dragon!");
                CompassTracker.endGame();
            }
        }
    }

    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item != null && item.getType() == Material.COMPASS) {
                if (item.getItemMeta().getDisplayName().contains("[Compass Tracker]") && CompassTracker.getInstance().gameStarted) {
                    if (CompassTracker.getInstance().speedrunner != null) {
                        if (!CompassTracker.getInstance().location.getWorld().getName().equals("world")) {
                            player.sendMessage(ChatColor.RED + "Tracker only works in the overworld!");
                        } else {
                            player.setCompassTarget(CompassTracker.getInstance().location);
                            player.sendMessage(ChatColor.GREEN + "Updating location of " + CompassTracker.getInstance().speedrunner.getName() + ".");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You haven't set a player! Do /settracker <player>.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (CompassTracker.getInstance().speedrunner == player) {
            CompassTracker.endGame();
            Bukkit.broadcastMessage(ChatColor.RED + CompassTracker.getInstance().speedrunner.getName() + " has left the server! Stopping game!");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (CompassTracker.getInstance().gameStarted && CompassTracker.getInstance().hunters.contains(player)) {
            CompassTracker.giveCompass(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player == CompassTracker.getInstance().speedrunner) {
            CompassTracker.endGame();
            Bukkit.broadcastMessage(ChatColor.RED + CompassTracker.getInstance().speedrunner.getName() + " has died! Stopping game!");
        }
    }
}
