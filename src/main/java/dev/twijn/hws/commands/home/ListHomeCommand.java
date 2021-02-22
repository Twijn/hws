package dev.twijn.hws.commands.home;

import dev.twijn.hws.manager.HomeManager;
import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.objects.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListHomeCommand implements CommandExecutor {

    private HomeManager manager;
    private LangManager lang;

    public ListHomeCommand(HomeManager manager, LangManager lang) {
        this.manager = manager;
        this.lang = lang;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            List<Home> homeList = manager.getHomes(player);
            int homeLimit = manager.getHomeLimit(player);

            StringBuilder homeStringBuilder = new StringBuilder();
            for (Home home : homeList) {
                if (!homeStringBuilder.toString().equals("")) {
                    homeStringBuilder.append(", ");
                }
                homeStringBuilder.append(home.getHomeName());
            }

            if (homeStringBuilder.toString().equals("")) {
                homeStringBuilder.append(lang.getNoPrefix("homes.no-homes"));
            }

            player.sendMessage(String.format(lang.getNormal("homes.list"), homeStringBuilder.toString(), homeList.size(), homeLimit == -1 ? "∞" : ""+homeLimit));
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }

        return true;
    }
}
