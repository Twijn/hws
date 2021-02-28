package dev.twijn.hws.commands.home;

import dev.twijn.hws.manager.HomeManager;
import dev.twijn.hws.objects.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCompleter implements TabCompleter {
    private HomeManager manager;

    public HomeCompleter(HomeManager manager) {
        this.manager = manager;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        List<String> list = new ArrayList<String>();

        for (Home home : manager.getHomes(player.getUniqueId())) {
            if (args.length == 0 || home.getHomeName().toLowerCase().startsWith(args[0].toLowerCase())) {
                list.add(home.getHomeName());
            }
        }

        return list;
    }
}
