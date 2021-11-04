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

package lol.hyper.compasstracker.events;

import lol.hyper.compasstracker.CompassTracker;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    private final CompassTracker compassTracker;

    public PlayerInteract(CompassTracker compassTracker) {
        this.compassTracker = compassTracker;
    }

    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {
        if (!compassTracker.gameManager.gameStatus()) {
            return;
        }

        if (!compassTracker.gameManager.trackingMode.equals("AUTO")) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item != null && item.getType() == Material.COMPASS) {
                if (item.getItemMeta().getDisplayName().contains("Tracking Compass")
                        && compassTracker.gameManager.gameStatus()) {
                    if (compassTracker.gameManager.getGameVersion() >= 16) {
                        Location speedrunnerLocation =
                                compassTracker.gameManager.getSpeedrunnerLocation(player.getWorld());
                        if (speedrunnerLocation == null) {
                            player.sendMessage(ChatColor.RED + "No location exists for this world!");
                            return;
                        }

                        compassTracker.gameManager.setHuntersLodestones();
                        player.sendMessage(ChatColor.GREEN + "Updating location of "
                                + compassTracker
                                .gameManager
                                .getGameSpeedrunner()
                                .getName() + ".");
                    } else {
                        Location speedrunnerLocation = compassTracker.gameManager.getSpeedrunnerLocation(null);
                        player.setCompassTarget(speedrunnerLocation);
                        player.sendMessage(ChatColor.GREEN + "Updating location of "
                                + compassTracker
                                .gameManager
                                .getGameSpeedrunner()
                                .getName() + ".");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You haven't set a player! Do /settracker <player>.");
                }
            }
        }
    }
}
