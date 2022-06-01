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
import lol.hyper.compasstracker.tools.GameManager;
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
        if (!compassTracker.gameManager.isGameRunning) {
            return;
        }

        if (compassTracker.gameManager.mode != GameManager.TrackingMode.MANUAL) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();
        int itemIndex = player.getInventory().getHeldItemSlot();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item != null && item.getType() == Material.COMPASS) {
                if (compassTracker.gameManager.checkCompass(player, itemIndex)) {
                    Location speedrunnerLocation;
                    if (compassTracker.gameManager.getGameVersion() >= 16) {
                        speedrunnerLocation = compassTracker.gameManager.getSpeedrunnerLocation(player.getWorld());
                        if (speedrunnerLocation == null) {
                            compassTracker.getAdventure().sender(player).sendMessage(compassTracker.getMessage("compass-right-click.no-location", null));
                            return;
                        }
                        compassTracker.gameManager.setHuntersLodestones();
                    } else {
                        speedrunnerLocation = compassTracker.gameManager.getSpeedrunnerLocation(null);
                        player.setCompassTarget(speedrunnerLocation);
                    }
                    compassTracker.getAdventure().sender(player).sendMessage(compassTracker.getMessage("compass-right-click.updating-location", compassTracker.gameManager.gameSpeedrunner.getName()));
                } else {
                    compassTracker.getAdventure().sender(player).sendMessage(compassTracker.getMessage("compass-right-click.no-player-set", null));
                }
            }
        }
    }
}
