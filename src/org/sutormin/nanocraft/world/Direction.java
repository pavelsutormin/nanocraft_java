package org.sutormin.nanocraft.world;

public enum Direction {
    UP,
    DOWN,
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public static Direction opposite(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }
}
