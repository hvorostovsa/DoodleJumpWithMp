package com.example.doodlejumpwithmp.controller.serverwork;

public enum ServerParameter {
    // general
    CODE("code"),
    CLIENT_ID("clientId"),

    // initial game parameters
    SEED("seed"),

    // info about new user connected
    NEW_ID("newId"),
    CONNECTIONS("connections"),

    // new info from user (as coordinates, speed and so on)
    DATA("data"),
    USERS("users"),

    // doodle parameters
    LOOSE("loose"),
    COORDINATE_X("coordinateX"),
    COORDINATE_Y("coordinateY"),
    MOVE_DIRECTION("moveDirection"),
    MOVE_SPEED("moveSpeed"),
    DOODLE_SIDE("doodleSide"),
    DIFF_Y("diffY"),
    SCORE("score"),
    ;

    private final String parameter;

    ServerParameter(String parameterName) {
        this.parameter = parameterName;
    }

    @Override
    public String toString() {
        return this.parameter;
    }
}
