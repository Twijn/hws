package dev.twijn.hws.manager.exceptions;

public class OutOfHomesException extends HomeException {

    public OutOfHomesException() {
        super("Not enough homes");
    }

}
