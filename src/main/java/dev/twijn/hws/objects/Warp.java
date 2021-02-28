package dev.twijn.hws.objects;

import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.UUID;

public class Warp extends OwnedPoint implements Comparable<Warp> {
    private String warpName;
    private int created;
    private int uses;

    public Warp(String warpName, UUID owner, Location location, int created, int uses) {
        super(owner, location);
        this.warpName = warpName;
        this.created = created;
        this.uses = uses;
    }

    public String getWarpName() {
        return warpName;
    }

    public int getCreated() {
        return created;
    }

    public int getUses() {return uses;}

    public void use() {
        uses++;
    }

    public int compareTo(Warp o) {
        int usesCompare = uses > o.getUses() ? -1
                : uses < o.getUses() ? 1
                : 0;
        return  usesCompare == 0
                ? 0 // potentially add second parameter for sorting here.
                : usesCompare;
    }
}
