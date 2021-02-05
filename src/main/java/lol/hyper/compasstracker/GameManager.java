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

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GameManager {

    private final CompassTracker compassTracker;

    private final ArrayList<Player> gameHunters = new ArrayList<>();
    private Player gameSpeedrunner =  null;
    private Boolean isGameRunning = false;
    private int trackerTask = 0;
    private Location speedrunnerLocation;

    public GameManager (CompassTracker compassTracker) {
        this.compassTracker = compassTracker;
    }

    /**
     * Adds hunter to the game.
     * @param playerToAdd Hunter to add.
     */
    public void addHunter(Player playerToAdd) {
        gameHunters.add(playerToAdd);
    }

    /**
     * Removes hunter from the game.
     * @param playerToRemove Hunter to remove.
     */
    public void removeHunter(Player playerToRemove) {
        gameHunters.remove(playerToRemove);
    }

    /**
     * Checks if hunter is actually a hunter.
     * @param hunterToCheck Hunter to check.
     * @return If hunter is on the list.
     */
    public Boolean isHunterListed(Player hunterToCheck) {
        return gameHunters.contains(hunterToCheck);
    }

    /**
     * Get the current hunters.
     * @return The hunters.
     */
    public ArrayList<Player> getGameHunters() {
        return gameHunters;
    }

    /**
     * Set the speedrunner for the game.
     * @param playerToSet Player to set.
     */
    public void setGameSpeedrunner(Player playerToSet) {
        gameSpeedrunner = playerToSet;
    }

    /**
     * Get who the current speedrunner is.
     * @return Player who is the speedrunner. Returns null if no one has been set yet.
     */
    public Player getGameSpeedrunner() {
        return gameSpeedrunner;
    }

    /**
     * Removes the set speed runner.
     */
    public void removeGameSpeedrunner() {
        gameSpeedrunner = null;
    }

    /**
     * Gets speedrunner location.
     * @return The location.
     */
    public Location getSpeedrunnerLocation() {
        return speedrunnerLocation;
    }

    /**
     * Gives the tracking compass.
     * @return The compass as an ItemStack.
     */
    public ItemStack trackingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("Tracking Compass");
        meta.getLore().add(ChatColor.DARK_PURPLE + "Right click to update " + gameSpeedrunner.getName() + "'s last location.");
        compass.setItemMeta(meta);
        return compass;
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        isGameRunning = true;
        for (Player player : gameHunters) {
            player.getInventory().addItem(trackingCompass());
        }
        trackerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(compassTracker, () -> {
            if (isGameRunning) {
                if (gameSpeedrunner.getWorld().getName().equals("world")) {
                    speedrunnerLocation = gameSpeedrunner.getLocation();
                }
            }
        }, 0L, 60);
        Bukkit.broadcastMessage(ChatColor.GREEN + "Game has started!");
    }

    /**
     * Ends the game.
     */
    public void endGame() {
        isGameRunning = false;
        gameSpeedrunner = null;
        for (Player hunters : gameHunters) {
            hunters.getInventory().remove(Material.COMPASS);
        }
        Bukkit.getScheduler().cancelTask(trackerTask);
    }

    /**
     * Get's the game status.
     * @return True if the game is running, false if the game is not running.
     */
    public Boolean gameStatus() {
        return isGameRunning;
    }


}