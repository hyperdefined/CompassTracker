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

package lol.hyper.compasstracker.tools;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoTrackingTask extends BukkitRunnable {

    private final GameManager gameManager;

    public AutoTrackingTask(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        World currentWorld = gameManager.gameSpeedrunner.getWorld();
        // save each location per world
        gameManager.speedrunnerLocations.put(currentWorld, gameManager.gameSpeedrunner.getLocation());
        if (gameManager.gameVersion >= 16) {
            gameManager.setHuntersLodestones();
        } else {
            for (Player player : gameManager.gameHunters) {
                player.setCompassTarget(gameManager.getSpeedrunnerLocation(null));
            }
        }
    }
}
