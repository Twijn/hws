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
import java.util.UUID;

public class ListWarpCommand implements CommandExecutor, TabCompleter {

    private static int WARPS_PER_PAGE = 8;

    private WarpManager manager;
    private LangManager lang;

    private ConnectionManager connectionManager;

    public ListWarpCommand(WarpManager manager, LangManager lang, ConnectionManager connectionManager) {
        this.manager = manager;
        this.lang = lang;
        this.connectionManager = connectionManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int totalWarps = manager.getWarpCount();
            int totalPages = (int)Math.max(1, Math.ceil((double)totalWarps / (double)WARPS_PER_PAGE));

            int page = 1;

            UUID target = null;

            if (args.length == 1) {
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
                    Connection con = null;
                    try {
                        con = connectionManager.createConnection();

                        PreparedStatement getPlayer = con.prepareStatement("select uuid from player where latest_name = ?;");
                        getPlayer.setString(1, args[0]);
                        ResultSet playerSet = getPlayer.executeQuery();

                        if (playerSet.next()) {
                            target = UUID.fromString(playerSet.getString(1));
                        } else {
                            player.sendMessage(String.format(lang.getError("warps.list.player-not-found-invalid-page"), args[0]));
                            return true;
                        }
                    } catch (Exception exception2) {
                        exception2.printStackTrace();
                    } finally {
                        try {
                            if (con != null && !con.isClosed()) con.close();
                        } catch (SQLException exception2) {
                            exception2.printStackTrace();
                        }
                    }
                }
            }

            List<Warp> warps = null;

            if (target == null) {
                warps = manager.getWarps(page, WARPS_PER_PAGE);
            } else {
                warps = manager.getWarps(target);
            }

            StringBuilder warpList = new StringBuilder(String.format(lang.getNormal("warps.list.header" + (target != null ? "-no-page": "")), page, totalPages, totalWarps));

            for (Warp warp : warps) {
                warpList.append("\n" + String.format(lang.getExtension("warps.list.item"), warp.getWarpName(), warp.getOwnerName(), warp.getLocation().getWorld().getName(), warp.getLocation().getBlockX(), warp.getLocation().getBlockY(), warp.getLocation().getBlockZ(), warp.getUses()));
            }

            if (warps.size() == 0) warpList.append("\n" + lang.getError("warps.list.no-warps"));

            player.sendMessage(warpList.toString());
        } else {
            sender.sendMessage(lang.getNoPrefix("in-game"));
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> pages = new ArrayList<String>();
        List<String> usernames = new ArrayList<String>();

        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement getUsernames = con.prepareStatement("select distinct player.latest_name from warp join player on player.uuid = warp.owner_uuid");
            ResultSet usernameSet = getUsernames.executeQuery();

            while (usernameSet.next()) {
                usernames.add(usernameSet.getString(1));
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

        for (int i = 1;i <= Math.ceil((double)manager.getWarpCount() / (double)WARPS_PER_PAGE);i++) {
            pages.add("" + i);
        }

        if (args.length <= 1) {
            List<String> autocomplete = new ArrayList<String>();
            String arg = "";

            if (args.length == 1) {
                arg = args[0].toLowerCase();
            }

            for (String username : usernames) {
                if (username.toLowerCase().startsWith(arg)) {
                    autocomplete.add(username);
                }
            }

            for (String page : pages) {
                if (page.toLowerCase().startsWith(arg)) {
                    autocomplete.add(page);
                }
            }

            return autocomplete;
        }

        return new ArrayList<String>();
    }
}
