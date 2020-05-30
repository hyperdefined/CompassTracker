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

public class CommandSetTracker implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("setplayer")) {
            if (sender.hasPermission("compasstracker.setplayer")) {
                if (CompassTracker.getInstance().gameStarted) {
                    sender.sendMessage(ChatColor.RED + "Cannot set player. There is a game right now.");
                } else {
                    if (Bukkit.getPlayerExact(args[1]) == null) {
                        sender.sendMessage(ChatColor.RED + "That player does not exist.");
                    } else {
                        CompassTracker.getInstance().speedrunner = Bukkit.getPlayerExact(args[1]);
                        sender.sendMessage(ChatColor.GREEN + args[1] + " has been set as the target player.");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permissions to set the player.");
            }
        } else if (args[0].equalsIgnoreCase("start")) {
            if (sender.hasPermission("compasstracker.start")) {
                if (CompassTracker.getInstance().gameStarted) {
                    sender.sendMessage(ChatColor.RED + "Game has started already!");
                } else {
                    if (CompassTracker.getInstance().speedrunner != null) {
                        CompassTracker.getInstance().gameStarted = true;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (CompassTracker.getInstance().speedrunner != player)  {
                                ItemStack compass = new ItemStack(Material.COMPASS);
                                ItemMeta meta = compass.getItemMeta();
                                meta.setDisplayName("[Tracker]");
                                compass.setItemMeta(meta);
                                player.getInventory().addItem(compass);
                            }
                        }
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(CompassTracker.getInstance(), () -> {
                            if (CompassTracker.getInstance().speedrunner.getWorld().getName().equals("world")) {
                                CompassTracker.getInstance().location = CompassTracker.getInstance().speedrunner.getLocation();
                            }
                        }, 0L, 60);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatColor.RED + CompassTracker.getInstance().speedrunner.getName() + " is now being tracked!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You have not set the target player. Use /ct setplayer <player>.");
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
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to stop the game.");
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
        } else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
            sender.sendMessage(ChatColor.YELLOW + "/ct help - Shows this menu.");
            sender.sendMessage(ChatColor.YELLOW + "/ct setplayer <player> - Set player to be tracked.");
            sender.sendMessage(ChatColor.YELLOW + "/ct removeplayer - Remove player from being tracked.");
            sender.sendMessage(ChatColor.YELLOW + "/ct start - Start the game. Player must be set.");
            sender.sendMessage(ChatColor.YELLOW + "/ct stop - Stops the game.");
            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid syntax! Do /ct help for the commands.");
        }
        return true;
    }
}
