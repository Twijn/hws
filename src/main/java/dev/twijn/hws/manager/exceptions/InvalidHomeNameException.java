package dev.twijn.hws.manager.exceptions;

public class InvalidHomeNameException extends HomeException {
    public InvalidHomeNameException() {
        super("Home name is not valid ");
    }
}
