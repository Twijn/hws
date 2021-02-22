package dev.twijn.hws.manager.exceptions;

public class InvalidWarpNameException extends WarpException {
    public InvalidWarpNameException() {
        super("Warp name is not valid ");
    }
}
