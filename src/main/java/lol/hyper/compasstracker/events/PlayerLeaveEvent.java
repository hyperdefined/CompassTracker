package lol.hyper.compasstracker.events;

import lol.hyper.compasstracker.CompassTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveEvent implements Listener {
    @EventHandler
    public void onPlayerJoinLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (CompassTracker.getInstance().speedrunner == player) {
            CompassTracker.endGame();
            Bukkit.broadcastMessage(ChatColor.RED + CompassTracker.getInstance().speedrunner.getName() + " has left the server! Stopping game!");
        }
    }
}
