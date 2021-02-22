package dev.twijn.hws.manager.exceptions;

public class OutOfWarpsException extends WarpException {

    public OutOfWarpsException() {
        super("Not enough warps");
    }

}
