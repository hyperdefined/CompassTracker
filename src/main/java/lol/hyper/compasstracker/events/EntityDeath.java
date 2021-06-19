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
import org.bukkit.entity.EntityType;
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
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            Player player = event.getEntity().getKiller();
            if (player == compassTracker.gameManager.getGameSpeedrunner()) {
                Bukkit.broadcastMessage(
                        ChatColor.GREEN + player.getName() + " has won the game! They killed the Ender Dragon!");
                compassTracker.gameManager.endGame();
            }
        }
    }
}
