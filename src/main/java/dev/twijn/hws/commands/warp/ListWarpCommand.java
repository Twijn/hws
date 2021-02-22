package dev.twijn.hws.commands.warp;

import dev.twijn.hws.manager.LangManager;
import dev.twijn.hws.manager.WarpManager;
import dev.twijn.hws.manager.connection.ConnectionManager;
import dev.twijn.hws.objects.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListWarpCommand implements CommandExecutor, TabCompleter {

    private static int WARPS_PER_PAGE = 15;

    private WarpManager manager;
    private LangManager lang;

    private ConnectionManager connectionManager;

    private List<String> autocomplete = null;

    public ListWarpCommand(WarpManager manager, LangManager lang, ConnectionManager connectionManager) {
        this.manager = manager;
        this.lang = lang;
        this.connectionManager = connectionManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int totalWarps = manager.getWarpCount();
            int totalPages = (int)Math.max(1, Math.ceil(totalWarps / WARPS_PER_PAGE));

            int page = 1;

            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                    if (page > totalPages) {
                        player.sendMessage(String.format(lang.getError("warps.list.page.over-total"), page, totalPages));
                        return true;
                    }
                    if (page < 1) {
                        player.sendMessage(String.format(lang.getError("warps.list.page.under-one"), page));
                        return true;
                    }
                } catch (NumberFormatException exception) {
                    player.sendMessage(String.format(lang.getError("warps.list.page.nan"), args[0]));
                    return true;
                }
            }

            StringBuilder warpList = new StringBuilder(String.format(lang.getNormal("warps.list.header"), page, totalPages, totalWarps));

            for (Warp warp : manager.getWarps(page, WARPS_PER_PAGE)) {
                warpList.append("\n" + String.format(lang.getExtension("warps.list.item"), warp.getWarpName(), warp.getOwnerName(), warp.getLocation().getWorld().getName(), warp.getLocation().getBlockX(), warp.getLocation().getBlockY(), warp.getLocation().getBlockZ()));
            }

            player.sendMessage(warpList.toString());
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }

        return true;
    }

    public void refreshTabComplete() {
        autocomplete = new ArrayList<String>();

        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement getUsernames = con.prepareStatement("select distinct player.latest_name from warp join player on player.uuid = warp.owner_uuid");
            ResultSet usernameSet = getUsernames.executeQuery();

            while (usernameSet.next()) {
                autocomplete.add(usernameSet.getString(1));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        for (int i = 1;i <= manager.getWarpCount();i++) {
            autocomplete.add("" + i);
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (autocomplete == null) refreshTabComplete();

        List<String> newAutocomplete = autocomplete;

        System.out.println(newAutocomplete);

        if (args.length == 1) {
            List<String> removeAutocomplete = new ArrayList<String>();
            for (String a : newAutocomplete) {
                if (!a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    removeAutocomplete.add(a);
                }
            }
            for (String a : removeAutocomplete) {
                newAutocomplete.remove(a);
            }
        }

        return newAutocomplete;
    }
}
