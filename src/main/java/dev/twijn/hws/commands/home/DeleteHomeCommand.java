package dev.twijn.hws.commands.home;

import dev.twijn.hws.manager.HomeManager;
import dev.twijn.hws.manager.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteHomeCommand implements CommandExecutor {

    private HomeManager homeManager;
    private LangManager lang;

    public DeleteHomeCommand(HomeManager homeManager, LangManager lang) {
        this.homeManager = homeManager;
        this.lang = lang;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            String homeName = args.length == 0 ? "home" : args[0];

            if (homeManager.deleteHome(player.getUniqueId(), homeName)) {
                player.sendMessage(String.format(lang.getNormal("homes.deleted"), homeName));
            } else {
                player.sendMessage(String.format(lang.getError("homes.does-not-exist"), homeName));
            }
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }

        return true;
    }

}
