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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        if (!compassTracker.gameManager.gameStatus()) {
            return;
        }

        Player player = event.getPlayer();
        if (compassTracker.gameManager.getGameSpeedrunner() == player) {
            Bukkit.broadcastMessage(ChatColor.RED
                    + compassTracker.gameManager.getGameSpeedrunner().getName()
                    + " has left the server! Stopping game!");
            compassTracker.gameManager.endGame(false);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!compassTracker.gameManager.gameStatus()) {
            return;
        }

        Player player = event.getEntity();
        if (player == compassTracker.gameManager.getGameSpeedrunner()) {
            Bukkit.broadcastMessage(ChatColor.RED
                    + compassTracker.gameManager.getGameSpeedrunner().getName() + " has died! Stopping game!");
            compassTracker.gameManager.endGame(false);
        }
    }
}
