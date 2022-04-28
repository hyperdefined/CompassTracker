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

import lol.hyper.compasstracker.tools.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    private final GameManager gameManager;

    public PlayerMove(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!gameManager.isGameRunning) {
            return;
        }

        Player player = event.getPlayer();
        if (gameManager.getGameSpeedrunner().getUniqueId() == player.getUniqueId()) {
            gameManager.speedrunnerLocations.put(player.getWorld(), player.getLocation());
        }
    }
}
