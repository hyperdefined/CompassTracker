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

import lol.hyper.compasstracker.CompassTracker;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GameManager {

    private final CompassTracker compassTracker;

    public final ArrayList<Player> gameHunters = new ArrayList<>();
    final String bukkitPackageName = Bukkit.getServer().getClass().getPackage().getName();
    public Player gameSpeedrunner = null;
    public Boolean isGameRunning = false;
    private long startTime;
    private AutoTrackingTask autoTrackingTask;
    public final int gameVersion;
    public final HashMap<World, Location> speedrunnerLocations = new HashMap<>();
    public String trackingMode = null;
    private final int trackingInterval;

    public GameManager(CompassTracker compassTracker) {
        this.compassTracker = compassTracker;
        String bukkitVersion = bukkitPackageName.substring(bukkitPackageName.lastIndexOf(".") + 1);
        gameVersion = Integer.parseInt(bukkitVersion.split("_")[1]);
        String mode = compassTracker.config.getString("tracking-mode");
        if (mode.equalsIgnoreCase("manual")) {
            trackingMode = "MANUAL";
        }
        if (mode.equalsIgnoreCase("auto")) {
            trackingMode = "AUTO";
        }
        if (trackingMode == null) {
            compassTracker.logger.warning("Invalid tracking mode!");
            endGame(false);
        }
        trackingInterval = compassTracker.config.getInt("auto-tracking-interval");
    }

    /**
     * Adds hunter to the game.
     *
     * @param playerToAdd Hunter to add.
     */
    public void addHunter(Player playerToAdd) {
        gameHunters.add(playerToAdd);
    }

    /**
     * Removes hunter from the game.
     *
     * @param playerToRemove Hunter to remove.
     */
    public void removeHunter(Player playerToRemove) {
        gameHunters.remove(playerToRemove);
    }

    /**
     * Checks if hunter is actually a hunter.
     *
     * @param hunterToCheck Hunter to check.
     * @return If hunter is on the list.
     */
    public Boolean isHunterListed(Player hunterToCheck) {
        return gameHunters.contains(hunterToCheck);
    }

    /**
     * Get the current hunters.
     *
     * @return The hunters.
     */
    public ArrayList<Player> getGameHunters() {
        return gameHunters;
    }

    /**
     * Get who the current speedrunner is.
     *
     * @return Player who is the speedrunner. Returns null if no one has been set yet.
     */
    public Player getGameSpeedrunner() {
        return gameSpeedrunner;
    }

    /**
     * Set the speedrunner for the game.
     *
     * @param playerToSet Player to set.
     */
    public void setGameSpeedrunner(Player playerToSet) {
        gameSpeedrunner = playerToSet;
    }

    /**
     * Removes the set speed runner.
     */
    public void removeGameSpeedrunner() {
        gameSpeedrunner = null;
    }

    /**
     * Gets speedrunner location.
     *
     * @return The location.
     */
    public Location getSpeedrunnerLocation(World world) {
        // default to overworld
        if (world == null) {
            return speedrunnerLocations.get(getOverworld());
        }
        return speedrunnerLocations.get(world);
    }

    /**
     * Gives the tracking compass.
     *
     * @return The compass as an ItemStack.
     */
    public ItemStack trackingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("Tracking Compass");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "Right click to update " + gameSpeedrunner.getName() + "'s last location.");
        meta.setLore(lore);
        // set some custom PDC on the compasses to track our own custom ones
        // probably a better way of doing this
        NamespacedKey key = new NamespacedKey(compassTracker, "tracking-compass");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        compass.setItemMeta(meta);
        return compass;
    }

    /**
     * Check to see if a given compass is our custom one.
     * We do index here because there could be a regular compass.
     * We don't want to remove all compass ItemStacks in the inventory.
     *
     * @param index The index in the player's inventory of the item.
     */
    public boolean checkCompass(Player player, int index) {
        NamespacedKey key = new NamespacedKey(compassTracker, "tracking-compass");
        ItemStack item = player.getInventory().getItem(index);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        return container.has(key, PersistentDataType.INTEGER);
    }

    /**
     * Get the server's main overworld. This only works if the server has just 1 single normal world.
     * There might be a much better way, but people can change the default world name, so I can't compare it to that.
     *
     * @return The overworld of the server.
     */
    public World getOverworld() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                return world;
            }
        }
        return null;
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        startTime = System.nanoTime();
        isGameRunning = true;

        getOverworld().setTime(0L);

        gameSpeedrunner.getInventory().clear();
        gameSpeedrunner.setHealth(20);
        gameSpeedrunner.setFoodLevel(20);
        gameSpeedrunner.setSaturation(20);

        for (Player player : gameHunters) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.getInventory().addItem(trackingCompass());
        }
        if (trackingMode.equals("AUTO")) {
            autoTrackingTask = new AutoTrackingTask(this);
            autoTrackingTask.runTaskTimer(compassTracker, 0, 20L * trackingInterval);
        } else {
            speedrunnerLocations.put(gameSpeedrunner.getWorld(), gameSpeedrunner.getLocation());
        }
        compassTracker.getAdventure().all().sendMessage(compassTracker.getMessage("game-start.start", null));
    }

    /**
     * Get the server's version.
     *
     * @return The version.
     */
    public int getGameVersion() {
        return gameVersion;
    }

    /**
     * Ends the game.
     */
    public void endGame(boolean won) {
        if (trackingMode.equals("AUTO")) {
            autoTrackingTask.cancel();
        }

        if (compassTracker.config.getBoolean("spawn-firework-on-win") && won) {
            spawnFirework(gameSpeedrunner);
        }

        speedrunnerLocations.clear();

        long timeElapsed = TimeUnit.SECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS);
        long hours = timeElapsed / 3600;
        long minutes = (timeElapsed % 3600) / 60;
        long seconds = timeElapsed % 60;

        gameSpeedrunner = null;
        isGameRunning = false;
        for (Player hunter : gameHunters) {
            PlayerInventory inv = hunter.getInventory();
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) == null) {
                    continue;
                }
                if (inv.getItem(i).getType() == Material.COMPASS) {
                    if (checkCompass(hunter, i)) {
                        inv.setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
        gameHunters.clear();
        String gameDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        compassTracker.getAdventure().all().sendMessage(compassTracker.getMessage("game-end.duration", gameDuration));
    }

    /**
     * Spawn a nice firework.
     *
     * @param player Player to spawn firework on.
     */
    private void spawnFirework(Player player) {
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fwmeta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder();

        fwmeta.addEffect(builder.withColor(Color.BLUE).flicker(true).build());
        fwmeta.addEffect(builder.trail(true).build());
        fwmeta.addEffect(builder.withFade(Color.RED).build());
        fwmeta.setPower(0);
        firework.setFireworkMeta(fwmeta);
    }

    /**
     * This will set all hunters in the game's lodestone. Only used for 1.16 and above.
     */
    public void setHuntersLodestones() {
        for (Player player : gameHunters) {
            Inventory playerInventory = player.getInventory();
            for (int i = 0; i < playerInventory.getSize(); i++) {
                ItemStack item = playerInventory.getItem(i);
                if (item != null) {
                    if (item.getType() == Material.COMPASS) {
                        if (checkCompass(player, i)) {
                            ItemMeta itemMeta = item.getItemMeta();
                            CompassMeta compassMeta = (CompassMeta) itemMeta;
                            if (compassMeta != null) {
                                compassMeta.setLodestoneTracked(false);
                                compassMeta.setLodestone(getSpeedrunnerLocation(player.getWorld()));
                                item.setItemMeta(compassMeta);
                            }
                        }
                    }
                }
            }
        }
    }
}
