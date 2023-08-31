package it.polimi.kp3d.domain;

import java.util.Objects;

public final class Cube {
    public final int w, d, h;
    public final int x, y, z;
    public final int maxX, maxY, maxZ;

    public Cube(final int w, final int d, final int h, final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.d = d;
        this.h = h;
        this.maxX = x + w;
        this.maxY = y + d;
        this.maxZ = z + h;
    }

    public Cube(final Cube toClone) {
        this.w = toClone.w;
        this.d = toClone.d;
        this.h = toClone.h;
        this.x = toClone.x;
        this.y = toClone.y;
        this.z = toClone.z;
        this.maxX = toClone.maxX;
        this.maxY = toClone.maxY;
        this.maxZ = toClone.maxZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public Cube clone() {
        return new Cube(w, d, h, x, y, z);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Cube))
            return false;
        final Cube cuboid = (Cube) o;
        return w == cuboid.w &&
                d == cuboid.d &&
                h == cuboid.h &&
                x == cuboid.x &&
                y == cuboid.y &&
                z == cuboid.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(w, d, h, x, y, z);
    }

    @Override
    public String toString() {
        return "Cube{" +
                "w=" + w +
                ", d=" + d +
                ", h=" + h +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    /*
     * COMPLEX METHODS
     */

    public boolean contains(Cube c) {
        return this.z <= c.z && c.maxZ <= this.maxZ &&
                this.y <= c.y && c.maxY <= this.maxY &&
                this.x <= c.x && c.maxX <= this.maxX;
    }

    public int getWidth() {
        return w;
    }

    public int getDepth() {
        return d;
    }

    public int getHeight() {
        return h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public long getSurfaceArea() {
        return w * h * 2L + d * h * 2L + w * d * 2L;
    }

    public long getVolume() {
        return (long) w * d * h;
    }

    public long getSideArea() {
        return w * h * 2L + d * h * 2L;
    }

    public long getArea() {
        return (long) w * d;
    }

    public Cube minimumBoundingCuboid(Cube other) {
        int smallX = Math.min(x, other.x);
        int smallY = Math.min(y, other.y);
        int smallZ = Math.min(z, other.z);
        int bigX = Math.max(getMaxX(), other.getMaxX());
        int bigY = Math.max(getMaxY(), other.getMaxY());
        int bigZ = Math.max(getMaxZ(), other.getMaxZ());
        return new Cube(bigX - smallX, bigY - smallY, bigZ - smallZ, smallX, smallY, smallZ);
    }
}
