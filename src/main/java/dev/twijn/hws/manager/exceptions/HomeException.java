package dev.twijn.hws.manager.exceptions;

public class HomeException extends Exception {

    public HomeException() {
        super("General home exception");
    }

    public HomeException(String msg) {
        super(msg);
    }

}
