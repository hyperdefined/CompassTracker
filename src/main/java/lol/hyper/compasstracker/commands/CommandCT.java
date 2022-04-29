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

package lol.hyper.compasstracker.commands;

import lol.hyper.compasstracker.CompassTracker;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandCT implements TabExecutor {

    private final CompassTracker compassTracker;

    private final List<String> commandArgs = Arrays.asList(
            "help",
            "setplayer",
            "removeplayer",
            "addhunter",
            "removehunter",
            "listhunters",
            "givecompass",
            "start",
            "stop");

    private final BukkitAudiences audiences;
    private final MiniMessage miniMessage;

    public CommandCT(CompassTracker compassTracker) {
        this.compassTracker = compassTracker;
        this.audiences = compassTracker.getAdventure();
        this.miniMessage = compassTracker.miniMessage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || sender instanceof ConsoleCommandSender) {
            audiences.sender(sender).sendMessage(miniMessage.deserialize("<green>CompassTracker version " + compassTracker.getDescription().getVersion() + ". Created by hyperdefined.</green>"));
            audiences.sender(sender).sendMessage(miniMessage.deserialize("<green>Use /ct help for command help.</green>"));
            return true;
        }

        switch (args[0]) {
            case "help": {
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<gold>-----------------Compass Tracker-----------------</gold>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct help - Shows this menu.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct setplayer <player> - Set player to be tracked.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct removeplayer - Remove player from being tracked.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct addhunter - Add player to hunter list. Hunters will get the tracking compass.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct removehunter - Remove hunter from list.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct listhunters - See who the hunter(s) are.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct givecompass - Give yourself the tracking compass. Hunters only.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct start - Start the game. Player and hunter(s) must be set.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>/ct stop - Stops the game.</yellow>"));
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<yellow>--------------------------------------------</yellow>"));
                break;
            }
            case "setplayer": {
                if (sender.hasPermission("compasstracker.setplayer")) {
                    if (compassTracker.gameManager.isGameRunning) {
                        audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.game-running", null));
                    } else {
                        Player player = Bukkit.getPlayerExact(args[1]);
                        if (player != null) {
                            if (!compassTracker.gameManager.getGameHunters().contains(Bukkit.getPlayerExact(args[1]))) {
                                compassTracker.gameManager.setGameSpeedrunner(Bukkit.getPlayerExact(args[1]));
                                audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.setplayer.target-player", player.getName()));
                            } else {
                                audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.setplayer.already-hunter", null));
                            }
                        } else {
                            audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.player-does-not-exist", null));
                        }
                    }
                } else {
                    audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.no-permission", null));
                }
                break;
            }
            case "removeplayer": {
                if (sender.hasPermission("compasstracker.removeplayer")) {
                    if (compassTracker.gameManager.isGameRunning) {
                        audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.game-running", null));
                    } else {
                        if (compassTracker.gameManager.getGameSpeedrunner() == null) {
                            audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.removeplayer.no-one-set", null));
                        } else {
                            String playerName = compassTracker.gameManager.getGameSpeedrunner().getName();
                            audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.removeplayer.removed", playerName));
                            compassTracker.gameManager.removeGameSpeedrunner();
                        }
                    }
                } else {
                    compassTracker.getAdventure().sender(sender).sendMessage(compassTracker.getMessage("errors.no-permission", null));
                }
                break;
            }
            case "addhunter": {
                if (sender.hasPermission("compasstracker.addhunter")) {
                    Player player = Bukkit.getPlayerExact(args[1]);
                    if (player != null) {
                        if (compassTracker.gameManager.getGameSpeedrunner() != player) {
                            if (!compassTracker.gameManager.isHunterListed(player)) {
                                compassTracker.gameManager.addHunter(player);
                                audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.addhunter.added", player.getName()));
                            } else {
                                audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.addhunter.already-hunter", player.getName()));
                            }
                        } else {
                            audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.addhunter.target-player", null));
                        }
                    } else {
                        audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.player-does-not-exist", null));
                    }
                } else {
                    audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.no-permission", null));
                }
                break;
            }
            case "removehunter": {
                if (sender.hasPermission("compasstracker.removehunter")) {
                    Player player = Bukkit.getPlayerExact(args[1]);
                    if (player != null) {
                        if (compassTracker.gameManager.isHunterListed(player)) {
                            compassTracker.gameManager.removeHunter(player);
                            audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.removehunter.removed", player.getName()));
                        } else {
                            audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.removehunter.not-a-hunter", player.getName()));
                        }
                    } else {
                        audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.player-does-not-exist", null));
                    }
                } else {
                    audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.no-permission", null));
                }
                break;
            }
            case "listhunters": {
                if (!compassTracker.gameManager.getGameHunters().isEmpty()) {
                    Set<String> huntersList = new HashSet<>();
                    compassTracker.gameManager.getGameHunters().forEach(player -> huntersList.add(player.getName()));
                    String huntersListFinal = StringUtils.join(huntersList, ",");
                    for (String line : compassTracker.getMessageList("commands.listhunters.command")) {
                        if (line.contains("%hunters%")) {
                            line = line.replace("%hunters%", huntersListFinal);
                        }
                        audiences.sender(sender).sendMessage(miniMessage.deserialize(line));
                    }
                } else {
                    audiences.sender(sender).sendMessage(compassTracker.getMessage("commands", null));
                }
                break;
            }
            case "givecompass": {
                Player player = (Player) sender;
                if (compassTracker.gameManager.getGameHunters().contains(player)) {
                    player.getInventory().addItem(compassTracker.gameManager.trackingCompass());
                } else {
                    audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.givecompass.not-a-hunter", null));
                }
                break;
            }
            case "start": {
                if (sender.hasPermission("compasstracker.start")) {
                    if (compassTracker.gameManager.isGameRunning) {
                        audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.game-running", null));
                    } else {
                        if (compassTracker.gameManager.getGameSpeedrunner() != null
                                && compassTracker.gameManager.getGameHunters().size() >= 1) {
                            compassTracker.gameManager.startGame();
                        } else {
                            audiences.sender(sender).sendMessage(compassTracker.getMessage("commands.start.not-ready", null));
                        }
                    }
                } else {
                    audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.no-permission", null));
                }
                break;
            }
            case "stop": {
                if (sender.hasPermission("compasstracker.stop")) {
                    if (!compassTracker.gameManager.isGameRunning) {
                        audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.no-game-running", null));
                    } else {
                        compassTracker.gameManager.endGame(false);
                        audiences.all().sendMessage(compassTracker.getMessage("game-end.stopped", null));
                    }
                } else {
                    audiences.sender(sender).sendMessage(compassTracker.getMessage("errors.no-permission", null));
                }
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!commandArgs.contains(args[0])) {
            return commandArgs;
        } else {
            return null;
        }
    }
}
