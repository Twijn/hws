package dev.twijn.hws.commands.spawn;

import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.manager.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnCommand implements CommandExecutor {

    private SpawnManager manager;
    private LangManager lang;
    private YamlConfiguration config;

    public SetSpawnCommand(SpawnManager manager, LangManager lang, YamlConfiguration config) {
        this.manager = manager;
        this.lang = lang;
        this.config = config;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            List<String> disallowList = config.getStringList("spawn.disallow-in-worlds");

            if (disallowList == null || !disallowList.contains(player.getWorld().getName())) {
                manager.setSpawnPoint(player.getLocation());

                player.sendMessage(String.format(lang.getNormal("spawn.set"), player.getWorld().getName()));
            } else {
                player.sendMessage(lang.getError("spawn.disallowed"));
            }
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }

        return true;
    }
}
