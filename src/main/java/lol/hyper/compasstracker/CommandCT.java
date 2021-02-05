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

import com.sun.org.glassfish.gmbal.GmbalException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class CommandCT implements TabExecutor {

    private final CompassTracker compassTracker;
    private final GameManager gameManager;

    public CommandCT(CompassTracker compassTracker, GameManager gameManager) {
        this.compassTracker = compassTracker;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.GREEN + "CompassTracker version " + compassTracker.getDescription().getVersion() + ". Created by hyperdefined.");
            sender.sendMessage(ChatColor.GREEN + "Use /ct help for command help.");
            return true;
        }

        switch (args[0]) {
            case "help":
                sender.sendMessage(ChatColor.GOLD + "-----------------Compass Tracker-----------------");
                sender.sendMessage(ChatColor.YELLOW + "/ct help - Shows this menu.");
                sender.sendMessage(ChatColor.YELLOW + "/ct setplayer <player> - Set player to be tracked.");
                sender.sendMessage(ChatColor.YELLOW + "/ct removeplayer - Remove player from being tracked.");
                sender.sendMessage(ChatColor.YELLOW + "/ct addhunter - Add player to hunter list. Hunters will get the tracking compass.");
                sender.sendMessage(ChatColor.YELLOW + "/ct removehunter - Remove hunter from list.");
                sender.sendMessage(ChatColor.YELLOW + "/ct listhunters - See who the hunter(s) are.");
                sender.sendMessage(ChatColor.YELLOW + "/ct givecompass - Give yourself the tracking compass. Hunters only.");
                sender.sendMessage(ChatColor.YELLOW + "/ct start - Start the game. Player and hunter(s) must be set.");
                sender.sendMessage(ChatColor.YELLOW + "/ct stop - Stops the game.");
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                break;
            case "setplayer":
                if (sender.hasPermission("compasstracker.setplayer")) {
                    if (gameManager.gameStatus()) {
                        sender.sendMessage(ChatColor.RED + "Cannot set player. There is a game right now.");
                    } else {
                        if (Bukkit.getPlayerExact(args[1]) != null) {
                            if (!gameManager.getGameHunters().contains(Bukkit.getPlayerExact(args[1]))) {
                                gameManager.setGameSpeedrunner(Bukkit.getPlayerExact(args[1]));
                                sender.sendMessage(ChatColor.GREEN + args[1] + " has been set as the target player.");
                            } else {
                                sender.sendMessage(ChatColor.RED + "That player is a hunter! Cannot set as player.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "That player does not exist.");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to set the player.");
                }
                break;
            case "removeplayer":
                if (sender.hasPermission("compasstracker.removeplayer")) {
                    if (gameManager.gameStatus()) {
                        sender.sendMessage(ChatColor.RED + "Cannot remove player. There is a game right now!");
                    } else {
                        if (gameManager.getGameSpeedrunner() == null) {
                            sender.sendMessage(ChatColor.RED + "Cannot remove player. There is not one set.");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + gameManager.getGameSpeedrunner().getName() + " has been removed.");
                            gameManager.removeGameSpeedrunner();
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission remove player from being tracked.");
                }
                break;
            case "addhunter":
                if (sender.hasPermission("compasstracker.addhunter")) {
                    if (Bukkit.getPlayerExact(args[1]) != null) {
                        if (gameManager.getGameSpeedrunner() != Bukkit.getPlayerExact(args[1])) {
                            if (!gameManager.isHunterListed(Bukkit.getPlayerExact(args[1]))) {
                                gameManager.addHunter(Bukkit.getPlayerExact(args[1]));
                                sender.sendMessage(ChatColor.GREEN + args[1] + " has been added as a hunter!");
                            } else {
                                sender.sendMessage(ChatColor.RED + args[1] + " is already set as a hunter.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "That player is being hunted! Cannot add as a hunter.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "That player does not exist.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission add a hunter.");
                }
                break;
            case "removehunter":
                if (sender.hasPermission("compasstracker.removehunter")) {
                    if (Bukkit.getPlayerExact(args[1]) != null) {
                        if (gameManager.isHunterListed(Bukkit.getPlayerExact(args[1]))) {
                            gameManager.removeHunter(Bukkit.getPlayerExact(args[1]));
                            sender.sendMessage(ChatColor.GREEN + args[1] + " has been removed as a hunter!");
                        } else {
                            sender.sendMessage(ChatColor.RED + args[1] + " is not a hunter.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "That player does not exist.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission remove a hunter.");
                }
                break;
            case "listhunters":
                if (!gameManager.getGameHunters().isEmpty()) {
                    sender.sendMessage(ChatColor.GOLD + "-----------------Hunters-----------------");
                    for (Player player : gameManager.getGameHunters()) {
                        sender.sendMessage(ChatColor.YELLOW + player.getName());
                    }
                    sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                } else {
                    sender.sendMessage(ChatColor.RED + "There are currently no hunters.");
                }
                break;
            case "givecompass":
                Player player = (Player) sender;
                if (gameManager.getGameHunters().contains(player)) {
                    player.getInventory().addItem(gameManager.trackingCompass());
                } else {
                    sender.sendMessage(ChatColor.RED + "You are not a hunter!");
                }
                break;
            case "start":
                if (sender.hasPermission("compasstracker.start")) {
                    if (gameManager.gameStatus()) {
                        sender.sendMessage(ChatColor.RED + "Game has started already!");
                    } else {
                        if (gameManager.getGameSpeedrunner() != null && gameManager.getGameHunters().size() >= 1) {
                            gameManager.startGame();
                        } else {
                            sender.sendMessage(ChatColor.RED + "Cannot start game. You have not set the player and/or hunter(s).");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to start the game.");
                }
                break;
            case "stop":
                if (sender.hasPermission("compasstracker.stop")) {
                    if (!gameManager.gameStatus()) {
                        sender.sendMessage(ChatColor.RED + "Game has not started yet!");
                    } else {
                        gameManager.endGame();
                        Bukkit.broadcastMessage(ChatColor.RED + "Game was stopped!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to stop the game.");
                }
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.asList("help", "setplayer", "removeplayer", "addhunter", "removehunter", "listhunters", "givecompass", "start", "stop");
    }
}