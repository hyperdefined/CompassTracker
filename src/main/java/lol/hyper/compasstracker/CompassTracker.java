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

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class CompassTracker extends JavaPlugin {

    public CommandCT commandCT;
    public Events events;
    public GameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);
        events = new Events(gameManager);
        commandCT = new CommandCT(this, gameManager);
        Bukkit.getPluginManager().registerEvents(events, this);
        this.getCommand("ct").setExecutor(commandCT);
        new UpdateChecker(this, 79938).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                this.getLogger().info("You are running the latest version.");
            } else {
                this.getLogger().info("There is a new version available! Please download at https://www.spigotmc.org/resources/compasstracker.79938/");
            }
        });
        Metrics metrics = new Metrics(this, 9389);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
