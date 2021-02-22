package dev.twijn.hws.commands.home;

import dev.twijn.hws.manager.HomeManager;
import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.manager.exceptions.HomeExistsException;
import dev.twijn.hws.manager.exceptions.InvalidHomeNameException;
import dev.twijn.hws.manager.exceptions.OutOfHomesException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private HomeManager manager = null;
    private LangManager lang = null;

    public SetHomeCommand(HomeManager manager, LangManager lang) {
        this.manager = manager;
        this.lang = lang;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            String homeName = args.length == 0 ? "home" : args[0];

            try {
                manager.setHome(player, homeName, player.getLocation());
                player.sendMessage(String.format(lang.getNormal("homes.set"), homeName));
            } catch (OutOfHomesException e) {
                player.sendMessage(String.format(lang.getError("homes.limit-reached"), manager.getHomeLimit(player)));
            } catch (HomeExistsException e) {
                player.sendMessage(String.format(lang.getError("homes.already-exists"), homeName));
            } catch (InvalidHomeNameException e) {
                player.sendMessage(String.format(lang.getError("homes.regex"), manager.getRegex()));
            }
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }
        return true;
    }

}
