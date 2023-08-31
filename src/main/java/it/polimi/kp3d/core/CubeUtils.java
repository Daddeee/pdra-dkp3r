package it.polimi.kp3d.core;

import it.polimi.kp3d.domain.Cube;

public class CubeUtils {
    public static Cube getBoundingBox(Cube a, Cube b) {
        int x = Math.min(a.getX(), b.getX());
        int y = Math.min(a.getY(), b.getY());
        int z = Math.min(a.getZ(), b.getZ());
        int width = Math.max(a.getMaxX(), b.getMaxX()) - x;
        int depth = Math.max(a.getMaxY(), b.getMaxY()) - y;
        int height = Math.max(a.getMaxZ(), b.getMaxZ()) - z;
        return new Cube(width, depth, height, x, y, z);
    }

    public static boolean overlap(Cube a, Cube b) {
        return overlapX(a, b) && overlapY(a, b) && overlapZ(a, b);
    }

    public static boolean overlapX(Cube a, Cube b) {
        return !(a.getX() + a.getWidth() <= b.getX() || a.getX() >= b.getX() + b.getWidth());
    }

    public static boolean overlapY(Cube a, Cube b) {
        return !(a.getY() + a.getDepth() <= b.getY() || a.getY() >= b.getY() + b.getDepth());
    }

    public static boolean overlapZ(Cube a, Cube b) {
        return !(a.getZ() + a.getHeight() <= b.getZ() || a.getZ() >= b.getZ() + b.getHeight());
    }

    public static boolean overlapXY(Cube a, Cube b) {
        return overlapX(a, b) && overlapY(a, b);
    }

    public static boolean overlapXZ(Cube a, Cube b) {
        return overlapX(a, b) && overlapZ(a, b);
    }

    public static boolean overlapYZ(Cube a, Cube b) {
        return overlapY(a, b) && overlapZ(a, b);
    }
}
