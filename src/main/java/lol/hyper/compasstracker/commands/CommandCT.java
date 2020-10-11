package lol.hyper.compasstracker.commands;

import lol.hyper.compasstracker.CompassTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandCT implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
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
        } else if (args[0].equalsIgnoreCase("setplayer")) {
            if (sender.hasPermission("compasstracker.setplayer")) {
                if (CompassTracker.getInstance().gameStarted) {
                    sender.sendMessage(ChatColor.RED + "Cannot set player. There is a game right now.");
                } else {
                    if (Bukkit.getPlayerExact(args[1]) != null) {
                        if (!CompassTracker.getInstance().hunters.contains(Bukkit.getPlayerExact(args[1]))) {
                            CompassTracker.getInstance().speedrunner = Bukkit.getPlayerExact(args[1]);
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
        } else if (args[0].equalsIgnoreCase("removeplayer")) {
            if (sender.hasPermission("compasstracker.removeplayer")) {
                if (CompassTracker.getInstance().gameStarted) {
                    sender.sendMessage(ChatColor.RED + "Cannot remove player. There is a game right now!");
                } else {
                    if (CompassTracker.getInstance().speedrunner == null) {
                        sender.sendMessage(ChatColor.RED + "Cannot remove player. There is not one set.");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + CompassTracker.getInstance().speedrunner.getName() + " has been removed.");
                        CompassTracker.getInstance().speedrunner = null;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission remove player from being tracked.");
            }
        } else if (args[0].equalsIgnoreCase("addhunter")) {
            if (sender.hasPermission("compasstracker.addhunter")) {
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    if (CompassTracker.getInstance().speedrunner != Bukkit.getPlayerExact(args[1])) {
                        if (!CompassTracker.getInstance().hunters.contains(Bukkit.getPlayerExact(args[1]))) {
                            CompassTracker.getInstance().hunters.add(Bukkit.getPlayerExact(args[1]));
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
        } else if (args[0].equalsIgnoreCase("removehunter")) {
            if (sender.hasPermission("compasstracker.removehunter")) {
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    if (CompassTracker.getInstance().hunters.contains(Bukkit.getPlayerExact(args[1]))) {
                        CompassTracker.getInstance().hunters.remove(Bukkit.getPlayerExact(args[1]));
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
        } else if (args[0].equalsIgnoreCase("listhunters")) {
            if (!CompassTracker.getInstance().hunters.isEmpty()) {
                sender.sendMessage(ChatColor.GOLD + "-----------------Hunters-----------------");
                for (Player player : CompassTracker.getInstance().hunters) {
                    sender.sendMessage(ChatColor.YELLOW + player.getName());
                }
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
            } else {
                sender.sendMessage(ChatColor.RED + "There are currently no hunters.");
            }
        } else if (args[0].equalsIgnoreCase("givecompass")) {
            Player player = (Player) sender;
            if (CompassTracker.getInstance().hunters.contains(player)) {
                ItemStack compass = new ItemStack(Material.COMPASS);
                ItemMeta meta = compass.getItemMeta();
                meta.setDisplayName("[Compass Tracker]");
                compass.setItemMeta(meta);
                player.getInventory().addItem(compass);
            } else {
                sender.sendMessage(ChatColor.RED + "You are not a hunter!");
            }
        } else if (args[0].equalsIgnoreCase("start")) {
            if (sender.hasPermission("compasstracker.start")) {
                if (CompassTracker.getInstance().gameStarted) {
                    sender.sendMessage(ChatColor.RED + "Game has started already!");
                } else {
                    if (CompassTracker.getInstance().speedrunner != null && CompassTracker.getInstance().hunters.size() >= 1) {
                        CompassTracker.startGame();
                    } else {
                        sender.sendMessage(ChatColor.RED + "Cannot start game. You have not set the player and/or hunter(s).");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to start the game.");
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (sender.hasPermission("compasstracker.stop")) {
                if (!CompassTracker.getInstance().gameStarted) {
                    sender.sendMessage(ChatColor.RED + "Game has not started yet!");
                } else {
                    CompassTracker.getInstance().gameStarted = false;
                    CompassTracker.getInstance().speedrunner = null;
                    for (Player player : CompassTracker.getInstance().hunters) {
                        player.getInventory().remove(Material.COMPASS);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to stop the game.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid syntax! Do /ct help for the commands.");
        }
        return true;
    }
}
