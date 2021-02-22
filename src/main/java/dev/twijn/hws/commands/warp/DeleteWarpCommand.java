package dev.twijn.hws.commands.warp;

import dev.twijn.hws.manager.HomeManager;
import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.manager.WarpManager;
import dev.twijn.hws.objects.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteWarpCommand implements CommandExecutor {

    private WarpManager warpManager;
    private LangManager lang;

    public DeleteWarpCommand(WarpManager warpManager, LangManager lang) {
        this.warpManager = warpManager;
        this.lang = lang;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                return false;
            }

            String warpName = args[0];

            Warp warp = warpManager.getWarp(warpName);

            if (warp == null) {
                player.sendMessage(String.format(lang.getError("warps.does-not-exist"), warpName));
                return true;
            }

            if (!warp.getOwner().equals(player.getUniqueId()) && !player.hasPermission("hws.warp.delete.others")) {
                player.sendMessage(lang.getError("warps.no-permission"));
                return true;
            }

            if (warpManager.deleteWarp(warpName)) {
                player.sendMessage(String.format(lang.getNormal("warps.deleted"), warpName));
            } else {
                player.sendMessage(String.format(lang.getError("warps.does-not-exist"), warpName));
            }
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }

        return true;
    }

}
