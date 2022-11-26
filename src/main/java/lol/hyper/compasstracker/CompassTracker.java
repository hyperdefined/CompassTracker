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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public final class CompassTracker extends JavaPlugin {

    public final File configFile = new File(this.getDataFolder(), "config.yml");
    public final File messagesFile = new File(this.getDataFolder(), "messages.yml");
    public final Logger logger = this.getLogger();
    public CommandCT commandCT;
    public EntityDeath entityDeath;
    public PlayerInteract playerInteract;
    public PlayerLeaveJoin playerLeaveJoin;
    public PlayerRespawn playerRespawn;
    public PlayerMove playerMove;
    public GameManager gameManager;
    public FileConfiguration config;
    public FileConfiguration messages;

    public final MiniMessage miniMessage = MiniMessage.miniMessage();
    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            logger.info("Copying default config!");
        }
        if (!messagesFile.exists()) {
            this.saveResource("messages.yml", true);
            logger.info("Copying default messages!");
        }
        loadConfig();
        gameManager = new GameManager(this);
        entityDeath = new EntityDeath(this);
        playerInteract = new PlayerInteract(this);
        playerLeaveJoin = new PlayerLeaveJoin(this);
        playerRespawn = new PlayerRespawn(this);
        playerMove = new PlayerMove(gameManager);
        commandCT = new CommandCT(this);

        Bukkit.getPluginManager().registerEvents(entityDeath, this);
        Bukkit.getPluginManager().registerEvents(playerInteract, this);
        Bukkit.getPluginManager().registerEvents(playerLeaveJoin, this);
        Bukkit.getPluginManager().registerEvents(playerRespawn, this);
        Bukkit.getPluginManager().registerEvents(playerMove, this);

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
        int CONFIG_VERSION = 2;
        if (config.getInt("config-version") != CONFIG_VERSION) {
            logger.warning("Your config file is outdated! Please regenerate the config.");
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        int MESSAGES_VERSION = 1;
        if (messages.getInt("version") != MESSAGES_VERSION) {
            logger.warning("Your messages file is outdated! Please regenerate this file!.");
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

    public BukkitAudiences getAdventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    /**
     * Gets a message from messages.yml.
     * @param path The path to the message.
     * @return Component with formatting applied.
     */
    public Component getMessage(String path, Object replacement) {
        String message = messages.getString(path);
        if (message == null) {
            logger.warning(path + " is not a valid message!");
            return Component.text("<Invalid path! " + path).color(NamedTextColor.RED);
        }
        if (message.contains("%player%")) {
            message = message.replace("%player%", replacement.toString());
        }
        if (message.contains("%time%")) {
            message = message.replace("%time%", replacement.toString());
        }
        if (message.contains("%hunters%")) {
            message = message.replace("%hunters%", replacement.toString());
        }
        return miniMessage.deserialize(message);
    }

    /**
     * Gets a message list.
     *
     * @param path The path to the message.
     * @return A raw string list of messages.
     */
    public List<String> getMessageList(String path) {
        List<String> messageList = messages.getStringList(path);
        if (!messageList.isEmpty()) {
            return messageList;
        }
        logger.warning(path + " is not a valid message!");
        messageList.add("<red>Invalid path! " + path + "</red>");
        return messageList;
    }
}
