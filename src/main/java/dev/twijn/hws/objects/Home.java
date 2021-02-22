package dev.twijn.hws.objects;

import org.bukkit.Location;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

public class Home extends OwnedPoint {
    private String homeName;
    private int created;

    public Home(String homeName, UUID owner, Location location, int created) {
        super(owner, location);
        this.homeName = homeName;
        this.created = created;
    }

    public String getHomeName() {
        return homeName;
    }

    public int getCreated() {
        return created;
    }
}
