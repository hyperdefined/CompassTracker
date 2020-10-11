package lol.hyper.compasstracker.events;

import lol.hyper.compasstracker.CompassTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityDeathEvent implements Listener {
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
}
