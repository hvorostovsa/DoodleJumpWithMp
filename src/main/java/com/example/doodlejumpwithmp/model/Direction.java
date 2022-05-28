package com.example.doodlejumpwithmp.model;

public enum Direction {
    LEFT(-1),
    RIGHT(1),
    NONE(0),
    ;

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Direction getByValue(int value) {
        return switch (value) {
            case -1 -> LEFT;
            case 1 -> RIGHT;
            case 0 -> NONE;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public Direction getOpposite() {
        return Direction.getByValue(-1 * value);
    }
}
