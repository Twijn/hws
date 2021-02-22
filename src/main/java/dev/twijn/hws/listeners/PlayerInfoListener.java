package dev.twijn.hws.listeners;

import dev.twijn.hws.HWSPlugin;
import dev.twijn.hws.manager.connection.ConnectionManager;
import dev.twijn.hws.utils.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class PlayerInfoListener implements Listener {

    private ConnectionManager connectionManager;

    public PlayerInfoListener() {
        connectionManager = HWSPlugin.getInstance().getConnectionManager();
    }

    private void update(Player player) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            int now = (int)Math.floor(new Date().getTime()/1000);

            PreparedStatement updateUser = con.prepareStatement("insert into player (uuid, latest_name, first_seen, last_seen) values (?, ?, ?, ?) on duplicate key update latest_name = ?, last_seen = ?;");
            updateUser.setString(1, player.getUniqueId().toString());
            updateUser.setString(2, player.getName());
            updateUser.setInt(3, now);
            updateUser.setInt(4, now);
            updateUser.setString(5, player.getName());
            updateUser.setInt(6, now);
            updateUser.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        update(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        update(e.getPlayer());
    }
}
