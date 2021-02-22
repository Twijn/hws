package dev.twijn.hws.listeners;

import dev.twijn.hws.manager.SpawnManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private SpawnManager manager;

    public RespawnListener(SpawnManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        if (!e.isBedSpawn() && !e.isAnchorSpawn()) {
            Location newSpawn = manager.getSpawnPoint(e.getRespawnLocation().getWorld());
            if (newSpawn != null) {
                e.setRespawnLocation(newSpawn);
            }
        }
    }

}
