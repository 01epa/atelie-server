package com.atelie.db.user;


public enum Role {
    ADMIN,
    USER,
    OWNER,
    ANONYMOUS;

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_OWNER = "ROLE_OWNER";
}