package dev.twijn.hws.commands.warp;

import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.manager.WarpManager;
import dev.twijn.hws.objects.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    private WarpManager manager = null;
    private LangManager lang = null;

    public WarpCommand(WarpManager manager, LangManager lang) {
        this.manager = manager;
        this.lang = lang;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                return false;
            }

            String warpName = args[0];

            Warp warp = manager.getWarp(warpName);

            if (warp != null) {
                player.teleport(warp.getLocation());
                player.sendMessage(String.format(lang.getNormal("warps.teleported"), warpName));

                manager.addUse(warp);
            } else {
                player.sendMessage(String.format(lang.getError("warps.does-not-exist"), warpName));
            }
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }
        return true;
    }

}
