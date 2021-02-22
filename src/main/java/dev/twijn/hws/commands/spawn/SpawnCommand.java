package dev.twijn.hws.commands.spawn;

import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.manager.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand implements CommandExecutor {

    private SpawnManager manager;
    private LangManager lang;

    private World disallowedDefault = null;
    private List<?> disallowedWorlds;

    public SpawnCommand(SpawnManager manager, LangManager lang, YamlConfiguration config) {
        this.manager = manager;
        this.lang = lang;

        String action = config.getString("spawn.disallow-action");

        if (!action.equalsIgnoreCase("disable")) {
            disallowedDefault = Bukkit.getWorld(action);
        }

        disallowedWorlds = config.getStringList("spawn.disallow-in-worlds");

        if (disallowedWorlds == null) disallowedWorlds = new ArrayList<String>();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (sender.hasPermission("hws.spawn.send")) {
                Player target = Bukkit.getPlayer(args[0]);

                if (target != null) {
                    World world = target.getWorld();

                    if (sender instanceof Player) {
                        world = ((Player)sender).getWorld();
                    }

                    if (disallowedWorlds.contains(world.getName())) {
                        if (disallowedDefault == null) {
                            target.sendMessage(String.format(lang.getError("spawn.disallowed")));
                            return true;
                        } else {
                            world = disallowedDefault;
                        }
                    }

                    Location spawn = manager.getSpawnPoint(world);

                    if (spawn == null) {
                        spawn = world.getSpawnLocation();
                    }

                    target.teleport(spawn);

                    target.sendMessage(String.format(lang.getNormal("spawn.teleported"), target.getWorld().getName()));
                    sender.sendMessage(String.format(lang.getNormal("spawn.sent"), target.getName(), target.getWorld().getName()));
                } else {
                    sender.sendMessage(String.format(lang.getError("spawn.target-not-found"), args[0]));
                }
            } else {
                sender.sendMessage(lang.getError("spawn.send-no-permission"));
            }
        } else if (sender instanceof Player) {
            Player player = (Player) sender;

            Location spawn = manager.getSpawnPoint(player.getWorld());

            if (spawn == null) {
                spawn = player.getWorld().getSpawnLocation();
            }

            player.teleport(spawn);

            player.sendMessage(String.format(lang.getNormal("spawn.teleported"), player.getWorld().getName()));
        }

        return true;
    }
}
