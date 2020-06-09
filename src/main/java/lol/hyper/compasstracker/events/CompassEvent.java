package lol.hyper.compasstracker.events;

import lol.hyper.compasstracker.CompassTracker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CompassEvent implements Listener {
    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (action.equals(Action.RIGHT_CLICK_AIR ) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
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
}
