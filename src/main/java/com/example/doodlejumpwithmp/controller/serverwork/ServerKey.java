package com.example.doodlejumpwithmp.controller.serverwork;

public enum ServerKey {
    CONNECTED, // when client wants to be added
    SET_ID, // server's response for client's CONNECTED
    NEW_USER_ADDED, // when we need to notify every user that there is a new user connected
    START_GAME, // when server starts game
    NEW_INFO, // send new information during game like position, speed, score and so on
    ;

    private final int code;

    ServerKey() {
        code = this.ordinal();
    }

    public int getCode() {
        return code;
    }

    public static ServerKey getKeyByCode(int code) {
        return ServerKey.values()[code];
    }
}
