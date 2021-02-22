package dev.twijn.hws.objects;

import dev.twijn.hws.HWSPlugin;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OwnedPoint extends Point {

    private UUID owner;

    public OwnedPoint(UUID owner, Location location) {
        super(location);
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        Connection con = null;
        try {
            con = HWSPlugin.getInstance().getConnectionManager().createConnection();

            PreparedStatement getOwnerName = con.prepareStatement("select latest_name from player where uuid = ?;");
            getOwnerName.setString(1, owner.toString());
            ResultSet ownerSet = getOwnerName.executeQuery();

            if (ownerSet.next()) {
                return ownerSet.getString(1);
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
}
