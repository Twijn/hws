package dev.twijn.hws.commands.warp;

import dev.twijn.hws.manager.WarpManager;
import dev.twijn.hws.objects.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCompleter implements TabCompleter {
    private WarpManager manager;

    public WarpCompleter(WarpManager manager) {
        this.manager = manager;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<String>();

        for (Warp warp : manager.getWarps()) {
            boolean hasPermission = true;

            if (sender instanceof Player) {
                hasPermission = warp.getOwner().equals(((Player)sender).getUniqueId());
            }

            if (args.length == 0 || warp.getWarpName().toLowerCase().startsWith(args[0].toLowerCase())) {
                if (cmd.getName().equalsIgnoreCase("deletewarp")
                        && !hasPermission
                        && !sender.hasPermission("hws.warp.delete.others")) {
                    continue;
                }
                list.add(warp.getWarpName());
            }
        }

        return list;
    }
}
