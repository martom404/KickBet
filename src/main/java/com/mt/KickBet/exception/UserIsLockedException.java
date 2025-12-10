package com.mt.KickBet.exception;

public class UserIsLockedException extends RuntimeException {
    public UserIsLockedException(String message) {
        super(message);
    }
}