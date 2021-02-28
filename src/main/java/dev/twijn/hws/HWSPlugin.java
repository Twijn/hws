package dev.twijn.hws;

import dev.twijn.hws.commands.home.*;
import dev.twijn.hws.commands.spawn.*;
import dev.twijn.hws.commands.warp.*;
import dev.twijn.hws.listeners.PlayerInfoListener;
import dev.twijn.hws.listeners.RespawnListener;
import dev.twijn.hws.manager.*;
import dev.twijn.hws.manager.connection.*;
import dev.twijn.hws.objects.Configuration;
import dev.twijn.hws.utils.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HWSPlugin extends JavaPlugin {
    private static HWSPlugin instance;

    private ConfigurationManager configurationManager;
    private ConnectionManager connectionManager;
    private LangManager langManager;

    private HomeManager homeManager;
    private WarpManager warpManager;
    private SpawnManager spawnManager;

    private List<Configuration> configurations;

    public static HWSPlugin getInstance() {
        return instance;
    }

    public void onEnable() {
        // Set current instance
        HWSPlugin.instance = this;

        // Create data folder
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();

        // Load configuration manager & validate configurations. This will load them automatically
        configurationManager = new ConfigurationManager(getDataFolder());
        configurations = configurationManager.generateConfigurations();
        configurationManager.validate(configurations);

        // Set up SQL
        YamlConfiguration config = getConfiguration("config.yml").getYAML();

        if (config.getString("database.engine").equalsIgnoreCase("mysql")) {
            getLogger().info("Using MySQL SQL engine");
            connectionManager = new MySQLConnectionManager(
                    config.getString("database.host"),
                    config.getString("database.database"),
                    config.getString("database.user"),
                    config.getString("database.password")
            );
        } else {
            getLogger().info("Using SQLite SQL engine");
            connectionManager = new SQLiteConnectionManager(getDataFolder());
        }

        // Test Connection
        Connection con = null;
        try {
            con = connectionManager.createConnection();
            getLogger().info("Connection to database was successful!");

            DatabaseUtils dbUtils = new DatabaseUtils();

            // Seed database
            dbUtils.seedDatabase(con);
        } catch (Exception exception) {
            exception.printStackTrace();

            getLogger().severe("Failed to create a connection. Disabling plugin");
            Bukkit.getPluginManager().disablePlugin(instance);
            return;
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        // Grab lang manager
        try {
            langManager = new LangManager();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            getLogger().severe("Failed to access a valid lang file. Disabling plugin");
            Bukkit.getPluginManager().disablePlugin(instance);
            return;
        }

        homeManager = new HomeManager();
        warpManager = new WarpManager();
        spawnManager = new SpawnManager();

        PluginManager pm = Bukkit.getPluginManager();

        // Add home limit permission nodes
        for (int i = 1;i <= 100; i++) {
            pm.addPermission(new Permission(
                    "hws.home.count." + i,
                    "Permission node for " + i + " home" + (i == 1 ? "" : "s"),
                    PermissionDefault.FALSE
            ));

            pm.addPermission(new Permission(
                    "hws.warp.count." + i,
                    "Permission node for " + i + " warp" + (i == 1 ? "" : "s"),
                    PermissionDefault.FALSE
            ));
        }

        // Register listeners
        pm.registerEvents(new PlayerInfoListener(), this);
        pm.registerEvents(new RespawnListener(spawnManager), this);

        // Add command executors
        getCommand("home").setExecutor(new HomeCommand(homeManager, langManager));
        getCommand("sethome").setExecutor(new SetHomeCommand(homeManager, langManager));
        getCommand("deletehome").setExecutor(new DeleteHomeCommand(homeManager, langManager));
        getCommand("listhome").setExecutor(new ListHomeCommand(homeManager, langManager));

        HomeCompleter homeCompleter = new HomeCompleter(homeManager);
        getCommand("home").setTabCompleter(homeCompleter);
        getCommand("deletehome").setTabCompleter(homeCompleter);

        getCommand("warp").setExecutor(new WarpCommand(warpManager, langManager));
        getCommand("setwarp").setExecutor(new SetWarpCommand(warpManager, langManager));
        getCommand("deletewarp").setExecutor(new DeleteWarpCommand(warpManager, langManager));

        WarpCompleter warpCompleter = new WarpCompleter(warpManager);
        getCommand("warp").setTabCompleter(warpCompleter);
        getCommand("deletewarp").setTabCompleter(warpCompleter);

        getCommand("listwarp").setExecutor(new ListWarpCommand(warpManager, langManager, connectionManager));

        getCommand("spawn").setExecutor(new SpawnCommand(spawnManager, langManager, getConfiguration("config.yml").getYAML()));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(spawnManager, langManager, getConfiguration("config.yml").getYAML()));
    }

    public Configuration getConfiguration(String name) {
        for (Configuration configuration : configurations) {
            if (configuration.getName().equalsIgnoreCase(name)) {
                return configuration;
            }
        }
        return null;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

}
