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
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {

    private final CompassTracker compassTracker;

    public EntityDeath(CompassTracker compassTracker) {
        this.compassTracker = compassTracker;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!compassTracker.gameManager.isGameRunning) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (compassTracker.gameManager.getGameSpeedrunner().equals(player)) {
                compassTracker.getAdventure().all().sendMessage(compassTracker.getMessage("game-end.hunters-win", player.getName()));
                compassTracker.gameManager.endGame(false);
            }
        }
        if (event.getEntity() instanceof EnderDragon) {
            Player player = event.getEntity().getKiller();
            if (player == compassTracker.gameManager.getGameSpeedrunner()) {
                compassTracker.getAdventure().all().sendMessage(compassTracker.getMessage("game-end.player-win", player.getName()));
                compassTracker.gameManager.endGame(true);
            }
        }
    }
}
