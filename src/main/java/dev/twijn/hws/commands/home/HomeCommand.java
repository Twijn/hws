package dev.twijn.hws.commands.home;

import dev.twijn.hws.manager.HomeManager;
import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.objects.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private HomeManager manager = null;
    private LangManager lang = null;

    public HomeCommand(HomeManager manager, LangManager lang) {
        this.manager = manager;
        this.lang = lang;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            String homeName = args.length == 0 ? "home" : args[0];

            Home home = manager.getHome(player.getUniqueId(), homeName);

            if (home != null) {
                player.teleport(home.getLocation());
                player.sendMessage(String.format(lang.getNormal("homes.teleported"), homeName));
            } else {
                player.sendMessage(String.format(lang.getError("homes.does-not-exist"), homeName));
            }
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }
        return true;
    }
}
