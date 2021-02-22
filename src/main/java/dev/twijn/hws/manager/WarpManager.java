package dev.twijn.hws.manager;

import dev.twijn.hws.HWSPlugin;
import dev.twijn.hws.manager.connection.ConnectionManager;
import dev.twijn.hws.manager.exceptions.WarpExistsException;
import dev.twijn.hws.manager.exceptions.InvalidWarpNameException;
import dev.twijn.hws.manager.exceptions.OutOfWarpsException;
import dev.twijn.hws.objects.Home;
import dev.twijn.hws.objects.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Warp management class
 *
 * @author Twijn
 */
public class WarpManager implements Manager {
    // We may want to cache this information later down the line.

    private ConnectionManager connectionManager;
    private String regex;
    public WarpManager() {
        connectionManager = HWSPlugin.getInstance().getConnectionManager();

        regex = HWSPlugin.getInstance().getConfiguration("config.yml").getYAML().getString("warps.regex");
    }

    public String getRegex() {
        return this.regex;
    }

    /**
     * Get warp limit for the specified player via permissions
     * @param player Player in question
     * @return Number of warps allowed, -1 = infinite, default of 1
     */
    public int getWarpLimit(Player player) {
        if (player.hasPermission("hws.warp.count.infinite")) {
            return -1;
        }

        for (int i = 100;i > 0; i--) {
            if (player.hasPermission("hws.warp.count." + i)) {
                return i;
            }
        }

        return 1;
    }

    public int getWarpCount() {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement getCount = con.prepareStatement("select count(name) from warp;");
            ResultSet countSet = getCount.executeQuery();

            if (countSet.next()) {
                return countSet.getInt(1);
            } else {
                return 0;
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

        return 0;
    }

    public List<Warp> getWarps(int page, int warpsPerPage) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            List<Warp> warps = new ArrayList<Warp>();

            PreparedStatement getWarps = con.prepareStatement("select name, owner_uuid, world, x, y, z, yaw, pitch, created from warp limit ?, ?;");
            getWarps.setInt(1, (page - 1) * warpsPerPage);
            getWarps.setInt(2, warpsPerPage);
            ResultSet warpSet = getWarps.executeQuery();

            while (warpSet.next()) {
                warps.add(new Warp(
                        warpSet.getString(1),
                        UUID.fromString(warpSet.getString(2)),
                        new Location(
                                Bukkit.getWorld(warpSet.getString(3)),
                                warpSet.getDouble(4),
                                warpSet.getDouble(5),
                                warpSet.getDouble(6),
                                warpSet.getFloat(7),
                                warpSet.getFloat(8)
                        ),
                        warpSet.getInt(9)
                ));
            }

            return warps;
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get set warps from the specified player
     * @param player The player to query
     * @return List of Home objects which the player has set
     */
    public List<Warp> getWarps(Player player) {
        return getWarps(player.getUniqueId());
    }

    /**
     * Get set warps from the specified UUID
     * @param uuid The UUID to query
     * @return List of Warp objects which has been set with the specified UUID
     */
    public List<Warp> getWarps(UUID uuid) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            List<Warp> warps = new ArrayList<Warp>();

            PreparedStatement getWarps = con.prepareStatement("select name, owner_uuid, world, x, y, z, yaw, pitch, created from warp where owner_uuid = ?;");
            getWarps.setString(1, uuid.toString());
            ResultSet warpSet = getWarps.executeQuery();

            while (warpSet.next()) {
                warps.add(new Warp(
                        warpSet.getString(1),
                        UUID.fromString(warpSet.getString(2)),
                        new Location(
                                Bukkit.getWorld(warpSet.getString(3)),
                                warpSet.getDouble(4),
                                warpSet.getDouble(5),
                                warpSet.getDouble(6),
                                warpSet.getFloat(7),
                                warpSet.getFloat(8)
                        ),
                        warpSet.getInt(9)
                        ));
            }

            return warps;
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get singular warp with the name of "warpName"
     * @param warpName Warp name of the Warp
     * @return The generated Warp object
     */
    public Warp getWarp(String warpName) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            List<Home> homes = new ArrayList<Home>();

            PreparedStatement getWarps = con.prepareStatement("select name, owner_uuid, world, x, y, z, yaw, pitch, created from warp where name = ?;");
            getWarps.setString(1, warpName);
            ResultSet warpSet = getWarps.executeQuery();

            if (warpSet.next()) {
                return new Warp(
                        warpSet.getString(1),
                        UUID.fromString(warpSet.getString(2)),
                        new Location(
                                Bukkit.getWorld(warpSet.getString(3)),
                                warpSet.getDouble(4),
                                warpSet.getDouble(5),
                                warpSet.getDouble(6),
                                warpSet.getFloat(7),
                                warpSet.getFloat(8)
                        ),
                        warpSet.getInt(9)
                );
            } else {
                return null;
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

        return null;
    }

    /**
     * Set a warp for Player, respecting the warp limit
     * @param player Player to use to set the warp
     * @param warpName Name of the warp
     * @param location Location of the Warp
     * @throws OutOfWarpsException When the player is out of warps to set
     * @throws WarpExistsException When a warp has already been set with "warpName"
     * @throws InvalidWarpNameException When the warp name does not match requirements
     */
    public void setWarp(Player player, String warpName, Location location) throws OutOfWarpsException, WarpExistsException, InvalidWarpNameException {
        int limit = getWarpLimit(player);
        if (limit == -1 || limit > getWarps(player).size()) {
            setWarp(player.getUniqueId(), warpName, location);
        } else {
            throw new OutOfWarpsException();
        }
    }

    /**
     * Sets a warp for UUID, NOT respecting the home limit
     * @param uuid UUID to use to set the warp
     * @param warpName Name of the warp
     * @param location Location of the warp
     * @throws WarpExistsException When the player has already set a warp with "warpName"
     * @throws InvalidWarpNameException When the warp name does not match requirements
     */
    public void setWarp(UUID uuid, String warpName, Location location) throws WarpExistsException, InvalidWarpNameException {
        if (!warpName.matches(regex)) {
            throw new InvalidWarpNameException();
        }

        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement checkExist = con.prepareStatement("select name from warp where name = ?;");
            checkExist.setString(1, warpName);
            ResultSet checkSet = checkExist.executeQuery();

            if (checkSet.next()) {
                throw new WarpExistsException();
            }

            PreparedStatement addWarp = con.prepareStatement("insert into warp (name, owner_uuid, world, x, y, z, pitch, yaw, created) values (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            addWarp.setString(1, warpName);
            addWarp.setString(2, uuid.toString());
            addWarp.setString(3, location.getWorld().getName());
            addWarp.setDouble(4, location.getX());
            addWarp.setDouble(5, location.getY());
            addWarp.setDouble(6, location.getZ());
            addWarp.setFloat(7, location.getPitch());
            addWarp.setFloat(8, location.getYaw());
            addWarp.setInt(9, (int)Math.floor(new Date().getTime() / 1000));
            addWarp.execute();
        } catch (WarpExistsException e) {
            throw e; // rethrow
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Deletes a warp with the Warp object
     * @param warp Warp to delete
     */
    public void deleteWarp(Warp warp) {
        deleteWarp(warp.getWarpName());
    }

    /**
     * Deletes a warp with the warp name
     * @param warpName Name of the home to delete
     */
    public boolean deleteWarp(String warpName) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement checkExists1 = con.prepareStatement("select name from warp where name = ?;");
            checkExists1.setString(1, warpName);
            ResultSet cers1 = checkExists1.executeQuery();

            if (cers1.next()) {
                PreparedStatement deleteStmt = con.prepareStatement("delete from warp where name = ?;");
                deleteStmt.setString(1, warpName);
                deleteStmt.execute();

                PreparedStatement checkExists2 = con.prepareStatement("select name from warp where name = ?;");
                checkExists2.setString(1, warpName);
                ResultSet cers2 = checkExists2.executeQuery();
                return !cers2.next();
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
        return false;
    }
}
