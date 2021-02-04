/*
 * This file is part of CompassTracker.
 *
 * CompassTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CompassTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CompassTracker.  If not, see <https://www.gnu.org/licenses/>.
 */

package lol.hyper.compasstracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

    private final CompassTracker compassTracker;

    public Events(CompassTracker compassTracker) {
        this.compassTracker = compassTracker;
    }

    @EventHandler
    public void onPlayerJoinLeave(EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            Player player = event.getEntity().getKiller();
            if (player == compassTracker.speedrunner) {
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won the game! They killed the Ender Dragon!");
                compassTracker.endGame();
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
                if (item.getItemMeta().getDisplayName().contains("[Compass Tracker]") && compassTracker.gameStarted) {
                    if (compassTracker.speedrunner != null) {
                        if (!compassTracker.location.getWorld().getName().equals("world")) {
                            player.sendMessage(ChatColor.RED + "Tracker only works in the overworld!");
                        } else {
                            player.setCompassTarget(compassTracker.location);
                            player.sendMessage(ChatColor.GREEN + "Updating location of " + compassTracker.speedrunner.getName() + ".");
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
        if (compassTracker.speedrunner == player) {
            compassTracker.endGame();
            Bukkit.broadcastMessage(ChatColor.RED + compassTracker.speedrunner.getName() + " has left the server! Stopping game!");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (compassTracker.gameStarted && compassTracker.hunters.contains(player)) {
            CompassTracker.giveCompass(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player == compassTracker.speedrunner) {
            compassTracker.endGame();
            Bukkit.broadcastMessage(ChatColor.RED + compassTracker.speedrunner.getName() + " has died! Stopping game!");
        }
    }
}
