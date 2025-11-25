package com.powerme.exception;

public class UserNotFoundException extends ServiceException {

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " not found");
    }

    public UserNotFoundException(String identifier) {
        super("User with identifier " + identifier + " not found");
    }
}
