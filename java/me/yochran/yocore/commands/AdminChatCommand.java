package me.yochran.yocore.commands;

import me.yochran.yocore.chats.ChatType;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminChatCommand implements CommandExecutor {

    private final yoCore plugin;

    public AdminChatCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.chats.admin")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.NoPermission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.IncorrectUsage")));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            if (plugin.achat_toggle.contains(((Player) sender).getUniqueId())) {
                plugin.achat_toggle.remove(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.ToggleOff")));
            } else {
                plugin.achat_toggle.add(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.ToggleOn")));
            }

            return true;
        }

        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = message + args[i] + " ";
        }

        for (Player admins : Bukkit.getOnlinePlayers()) {
            if (admins.hasPermission("yocore.chats.admin"))
                ChatType.sendMessage((Player) sender, admins, ChatType.ADMIN, message);
        }

        return true;
    }
}
