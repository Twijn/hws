package dev.twijn.hws.manager.exceptions;

public class HomeExistsException extends HomeException {

    public HomeExistsException() {
        super("Home already exists with this name and UUID");
    }

}
