package com.powerme.exception;

public class UserNotFoundException extends ServiceException {

    public UserNotFoundException() {
        super("User not found");
    }
}
