package dev.twijn.hws.objects;

import org.bukkit.Location;

public class Point {

    private Location location;

    public Point(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

}
