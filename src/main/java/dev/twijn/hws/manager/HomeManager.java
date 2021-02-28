package dev.twijn.hws.manager;

import dev.twijn.hws.HWSPlugin;
import dev.twijn.hws.manager.connection.ConnectionManager;
import dev.twijn.hws.manager.exceptions.HomeExistsException;
import dev.twijn.hws.manager.exceptions.InvalidHomeNameException;
import dev.twijn.hws.manager.exceptions.OutOfHomesException;
import dev.twijn.hws.objects.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Home management class
 *
 * @author Twijn
 */
public class HomeManager implements Manager {
    private Map<UUID, Map<String, Home>> homeList = new HashMap<UUID, Map<String, Home>>();

    private ConnectionManager connectionManager;
    private String regex;
    public HomeManager() {
        connectionManager = HWSPlugin.getInstance().getConnectionManager();

        regex = HWSPlugin.getInstance().getConfiguration("config.yml").getYAML().getString("homes.regex");

        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement getHomes = con.prepareStatement("select name, owner_uuid, world, x, y, z, yaw, pitch, created from home;");
            ResultSet homeSet = getHomes.executeQuery();

            while (homeSet.next()) {
                UUID owner = UUID.fromString(homeSet.getString(2));
                if (!homeList.containsKey(owner)) {
                    homeList.put(owner, new TreeMap<String, Home>());
                }

                homeList.get(owner).put(homeSet.getString(1).toLowerCase(), new Home(
                        homeSet.getString(1),
                        owner,
                        new Location(
                                Bukkit.getWorld(homeSet.getString(3)),
                                homeSet.getDouble(4),
                                homeSet.getDouble(5),
                                homeSet.getDouble(6),
                                homeSet.getFloat(7),
                                homeSet.getFloat(8)
                        ),
                        homeSet.getInt(9)
                ));
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
    }

    public String getRegex() {
        return this.regex;
    }

    /**
     * Get home limit for the specified player via permissions
     * @param player Player in question
     * @return Number of homes allowed, -1 = infinite, default of 1
     */
    public int getHomeLimit(Player player) {
        if (player.hasPermission("hws.home.count.infinite")) {
            return -1;
        }

        for (int i = 100;i > 0; i--) {
            if (player.hasPermission("hws.home.count." + i)) {
                return i;
            }
        }

        return 1;
    }

    /**
     * Get set homes for the specified player
     * @param player The player to query
     * @return List of Home objects which the player has set
     */
    public List<Home> getHomes(Player player) {
        return getHomes(player.getUniqueId());
    }

    /**
     * Get set homes for the specified UUID
     * @param uuid The UUID to query
     * @return List of Home objects which has been set with the specified UUID
     */
    public List<Home> getHomes(UUID uuid) {
        return homeList.containsKey(uuid) ? new ArrayList<Home>(homeList.get(uuid).values()) : new ArrayList<Home>();
    }

    /**
     * Get singular home with the name of "homeName"
     * @param uuid Owner UUID of the home
     * @param homeName Home name of the home
     * @return The generated Home object
     */
    public Home getHome(UUID uuid, String homeName) {
        if (homeList.containsKey(uuid) && homeList.get(uuid).containsKey(homeName.toLowerCase())) {
            return homeList.get(uuid).get(homeName.toLowerCase());
        }
        return null;
    }

    /**
     * Set a home for Player, respecting the home limit
     * @param player Player to use to set the home
     * @param homeName Name of the home
     * @param location Location of the home
     * @throws OutOfHomesException When the player is out of homes to set
     * @throws HomeExistsException When the player has already set a home with "homeName"
     * @throws InvalidHomeNameException When the home name does not match requirements
     */
    public void setHome(Player player, String homeName, Location location) throws OutOfHomesException, HomeExistsException, InvalidHomeNameException {
        int limit = getHomeLimit(player);
        if (limit == -1 || limit > getHomes(player).size()) {
            setHome(player.getUniqueId(), homeName, location);
        } else {
            throw new OutOfHomesException();
        }
    }

    /**
     * Sets a home for UUID, NOT respecting the home limit
     * @param uuid UUID to use to set the home
     * @param homeName Name of the home
     * @param location Location of the home
     * @throws HomeExistsException When the player has already set a home with "homeName"
     * @throws InvalidHomeNameException When the home name does not match requirements
     */
    public void setHome(UUID uuid, String homeName, Location location) throws HomeExistsException, InvalidHomeNameException {
        if (!homeName.matches(regex)) {
            throw new InvalidHomeNameException();
        }

        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement checkExist = con.prepareStatement("select name from home where owner_uuid = ? and name = ?;");
            checkExist.setString(1, uuid.toString());
            checkExist.setString(2, homeName);
            ResultSet checkSet = checkExist.executeQuery();

            if (checkSet.next()) {
                throw new HomeExistsException();
            }

            PreparedStatement addHome = con.prepareStatement("insert into home (name, owner_uuid, world, x, y, z, pitch, yaw, created) values (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            addHome.setString(1, homeName);
            addHome.setString(2, uuid.toString());
            addHome.setString(3, location.getWorld().getName());
            addHome.setDouble(4, location.getX());
            addHome.setDouble(5, location.getY());
            addHome.setDouble(6, location.getZ());
            addHome.setFloat(7, location.getPitch());
            addHome.setFloat(8, location.getYaw());
            addHome.setInt(9, (int)Math.floor(new Date().getTime() / 1000));
            addHome.execute();

            if (!homeList.containsKey(uuid)) {
                homeList.put(uuid, new TreeMap<String, Home>());
            }

            homeList.get(uuid).put(homeName.toLowerCase(), new Home(
                    homeName,
                    uuid,
                    location,
                    (int)Math.floor(new Date().getTime() / 1000)
            ));
        } catch (HomeExistsException e) {
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
     * Deletes a home with the Home object
     * @param home Home to delete
     */
    public void deleteHome(Home home) {
        deleteHome(home.getOwner(), home.getHomeName());
    }

    /**
     * Deletes a home with the player's UUID and home name
     * @param uuid UUID of the home to delete
     * @param homeName Name of the home to delete
     */
    public boolean deleteHome(UUID uuid, String homeName) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement checkExists1 = con.prepareStatement("select name from home where owner_uuid = ? and name = ?;");
            checkExists1.setString(1, uuid.toString());
            checkExists1.setString(2, homeName);
            ResultSet cers1 = checkExists1.executeQuery();

            if (cers1.next()) {
                PreparedStatement deleteStmt = con.prepareStatement("delete from home where owner_uuid = ? and name = ?;");
                deleteStmt.setString(1, uuid.toString());
                deleteStmt.setString(2, homeName);
                deleteStmt.execute();

                PreparedStatement checkExists2 = con.prepareStatement("select name from home where owner_uuid = ? and name = ?;");
                checkExists2.setString(1, uuid.toString());
                checkExists2.setString(2, homeName);
                ResultSet cers2 = checkExists2.executeQuery();

                if (homeList.containsKey(uuid)) {
                    homeList.get(uuid).remove(homeName.toLowerCase());

                    if (homeList.get(uuid).size() == 0) {
                        homeList.remove(uuid);
                    }
                }
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
