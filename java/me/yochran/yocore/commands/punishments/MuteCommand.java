package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public MuteCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.mute")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.InvalidPlayer")));
            return true;
        }

        if (plugin.muted_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.TargetIsMuted")));
            return true;
        }

        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason = reason + args[i] + " ";
        }

        String executor;
        String executorName;
        if (!(sender instanceof Player)) {
            executor = "CONSOLE";
            executorName = "&c&lConsole";
        } else {
            executor = ((Player) sender).getUniqueId().toString();
            executorName = playerManagement.getPlayerColor((Player) sender);
        }

        boolean silent = false;
        if (reason.contains("-s")) {
            reason = reason.replace("-s", "");
            silent = true;
        }

        punishmentManagement.addInfraction("Mute", target, executor, reason, "Permanent", silent);
        punishmentManagement.addMute(target, false);

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Mute.Permanent.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", reason)));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", reason)));
        }

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getName()).sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.TargetMessage")
                    .replace("%reason%", reason)));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Mute.Permanent.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
