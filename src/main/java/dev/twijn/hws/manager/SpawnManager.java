package dev.twijn.hws.manager;

import dev.twijn.hws.HWSPlugin;
import dev.twijn.hws.manager.connection.ConnectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpawnManager implements Manager {

    private static double ROUNDING_FACTOR = 2;

    private ConnectionManager connectionManager;
    public SpawnManager() {
        connectionManager = HWSPlugin.getInstance().getConnectionManager();
    }

    public Location getSpawnPoint(World world) {
        return getSpawnPoint(world.getName());
    }

    public Location getSpawnPoint(String worldName) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            PreparedStatement getSpawn = con.prepareStatement("select world, x, y, z, yaw, pitch from spawn where world = ?;");
            getSpawn.setString(1, worldName);
            ResultSet spawnSet = getSpawn.executeQuery();

            if (spawnSet.next()) {
                return new Location(
                        Bukkit.getWorld(spawnSet.getString(1)),
                        spawnSet.getDouble(2),
                        spawnSet.getDouble(3),
                        spawnSet.getDouble(4),
                        spawnSet.getFloat(5),
                        spawnSet.getFloat(6)
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

    public void setSpawnPoint(Location location) {
        Connection con = null;
        try {
            con = connectionManager.createConnection();

            location.setX(Math.floor(location.getX() * ROUNDING_FACTOR)/ROUNDING_FACTOR);
            location.setY(Math.floor(location.getY() * ROUNDING_FACTOR)/ROUNDING_FACTOR);
            location.setZ(Math.floor(location.getZ() * ROUNDING_FACTOR)/ROUNDING_FACTOR);

            PreparedStatement insertSpawn = con.prepareStatement("insert into spawn (world, x, y, z, pitch, yaw) values (?, ?, ?, ?, ?, ?) on duplicate key update x = ?, y = ?, z = ?, pitch = ?, yaw = ?;");
            insertSpawn.setString(1, location.getWorld().getName());
            insertSpawn.setDouble(2, location.getX());
            insertSpawn.setDouble(3, location.getY());
            insertSpawn.setDouble(4, location.getZ());
            insertSpawn.setFloat(5, location.getPitch());
            insertSpawn.setFloat(6, location.getYaw());
            insertSpawn.setDouble(7, location.getX());
            insertSpawn.setDouble(8, location.getY());
            insertSpawn.setDouble(9, location.getZ());
            insertSpawn.setFloat(10, location.getPitch());
            insertSpawn.setFloat(11, location.getYaw());
            insertSpawn.execute();

            location.getWorld().setSpawnLocation(location);
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

}
