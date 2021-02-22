package dev.twijn.hws.objects;

import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.UUID;

public class Warp extends OwnedPoint {
    private String warpName;
    private int created;

    public Warp(String warpName, UUID owner, Location location, int created) {
        super(owner, location);
        this.warpName = warpName;
        this.created = created;
    }

    public String getWarpName() {
        return warpName;
    }

    public int getCreated() {
        return created;
    }
}
