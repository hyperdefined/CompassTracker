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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveJoin implements Listener {

    private final CompassTracker compassTracker;

    public PlayerLeaveJoin(CompassTracker compassTracker) {
        this.compassTracker = compassTracker;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!compassTracker.gameManager.isGameRunning) {
            return;
        }

        Player player = event.getPlayer();
        if (compassTracker.gameManager.getGameSpeedrunner() == player) {
            compassTracker.getAdventure().all().sendMessage(compassTracker.getMessage("game-end.player-leave", player.getName()));
            compassTracker.gameManager.endGame(false);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!compassTracker.gameManager.isGameRunning) {
            return;
        }

        Player player = event.getEntity();
        if (player == compassTracker.gameManager.getGameSpeedrunner()) {
            compassTracker.getAdventure().all().sendMessage(compassTracker.getMessage("game-end.player-died", player.getName()));
            compassTracker.gameManager.endGame(false);
        }
    }
}
