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

import lol.hyper.compasstracker.commands.CommandCT;
import lol.hyper.compasstracker.events.*;
import lol.hyper.compasstracker.tools.GameManager;
import lol.hyper.githubreleaseapi.GitHubRelease;
import lol.hyper.githubreleaseapi.GitHubReleaseAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class CompassTracker extends JavaPlugin {

    public final File configFile = new File(this.getDataFolder(), "config.yml");
    public final Logger logger = this.getLogger();
    final int configVersion = 2;
    public CommandCT commandCT;
    public EntityDeath entityDeath;
    public PlayerInteract playerInteract;
    public PlayerLeaveJoin playerLeaveJoin;
    public PlayerRespawn playerRespawn;
    public GameManager gameManager;
    public FileConfiguration config;

    @Override
    public void onEnable() {
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            logger.info("Copying default config!");
        }
        loadConfig();
        gameManager = new GameManager(this);
        entityDeath = new EntityDeath(this);
        playerInteract = new PlayerInteract(this);
        playerLeaveJoin = new PlayerLeaveJoin(this);
        playerRespawn = new PlayerRespawn(this);

        commandCT = new CommandCT(this);

        Bukkit.getPluginManager().registerEvents(entityDeath, this);
        Bukkit.getPluginManager().registerEvents(playerInteract, this);
        Bukkit.getPluginManager().registerEvents(playerLeaveJoin, this);
        Bukkit.getPluginManager().registerEvents(playerRespawn, this);
        if (config.getString("tracking-mode").equalsIgnoreCase("AUTO")) {
            Bukkit.getPluginManager().registerEvents(new PlayerMove(gameManager), this);
        }

        this.getCommand("ct").setExecutor(commandCT);

        new Metrics(this, 9389);

        Bukkit.getScheduler().runTaskAsynchronously(this, this::checkForUpdates);
    }

    @Override
    public void onDisable() {
        if (gameManager.isGameRunning) {
            gameManager.endGame(false);
        }
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        if (config.getInt("config-version") != configVersion) {
            logger.warning("Your config file is outdated! Please regenerate the config.");
        }
    }

    public void checkForUpdates() {
        GitHubReleaseAPI api;
        try {
            api = new GitHubReleaseAPI("CompassTracker", "hyperdefined");
        } catch (IOException e) {
            logger.warning("Unable to check updates!");
            e.printStackTrace();
            return;
        }
        GitHubRelease current = api.getReleaseByTag(this.getDescription().getVersion());
        GitHubRelease latest = api.getLatestVersion();
        if (current == null) {
            logger.warning("You are running a version that does not exist on GitHub. If you are in a dev environment, you can ignore this. Otherwise, this is a bug!");
            return;
        }
        int buildsBehind = api.getBuildsBehind(current);
        if (buildsBehind == 0) {
            logger.info("You are running the latest version.");
        } else {
            logger.warning("A new version is available (" + latest.getTagVersion() + ")! You are running version " + current.getTagVersion() + ". You are " + buildsBehind + " version(s) behind.");
        }
    }
}
