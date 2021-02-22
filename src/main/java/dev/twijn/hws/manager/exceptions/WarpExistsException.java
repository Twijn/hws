package dev.twijn.hws.manager.exceptions;

public class WarpExistsException extends WarpException {

    public WarpExistsException() {
        super("Warp already exists with this name");
    }

}
