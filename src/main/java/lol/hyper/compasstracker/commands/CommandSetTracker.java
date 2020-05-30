package lol.hyper.compasstracker.commands;

import lol.hyper.compasstracker.CompassTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetTracker implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("compasstracker.start")) {
            CompassTracker.getInstance().speedrunner = Bukkit.getPlayerExact(args[0]);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(CompassTracker.getInstance(), () -> {
                if (CompassTracker.getInstance().speedrunner.getWorld().getName().equals("world")) {
                    CompassTracker.getInstance().location = CompassTracker.getInstance().speedrunner.getLocation();
                }
            }, 0L, 60);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + CompassTracker.getInstance().speedrunner.getName() + " is now being tracked!");
            }
        }
        return true;
    }
}
