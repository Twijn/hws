package dev.twijn.hws.manager.exceptions;

public class WarpException extends Exception {

    public WarpException() {
        super("General warp exception");
    }

    public WarpException(String msg) {
        super(msg);
    }

}
