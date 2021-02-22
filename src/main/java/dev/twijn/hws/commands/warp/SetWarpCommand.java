package dev.twijn.hws.commands.warp;

import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.manager.WarpManager;
import dev.twijn.hws.manager.exceptions.WarpExistsException;
import dev.twijn.hws.manager.exceptions.InvalidWarpNameException;
import dev.twijn.hws.manager.exceptions.OutOfWarpsException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {

    private WarpManager manager = null;
    private LangManager lang = null;

    public SetWarpCommand(WarpManager manager, LangManager lang) {
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

            try {
                manager.setWarp(player, warpName, player.getLocation());
                player.sendMessage(String.format(lang.getNormal("warps.set"), warpName));
            } catch (OutOfWarpsException e) {
                player.sendMessage(String.format(lang.getError("warps.limit-reached"), manager.getWarpLimit(player)));
            } catch (WarpExistsException e) {
                player.sendMessage(String.format(lang.getError("warps.already-exists"), warpName));
            } catch (InvalidWarpNameException e) {
                player.sendMessage(String.format(lang.getError("warps.regex"), manager.getRegex()));
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(lang.getError("warps.unknown"));
            }
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }
        return true;
    }

}
