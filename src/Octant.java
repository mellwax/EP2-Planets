import codedraw.CodeDraw;

import java.awt.*;

public class Octant {

    private final double x;
    private final double y;
    private final double z;
    private final double length;

    private double mass;
    private Vector3 massCenter;

    public Octant(double x, double y, double z, double length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = length;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getLength() {
        return length;
    }

    public boolean contains(Vector3 massCenter) {
        return massCenter.x >= x && massCenter.x < (x + length) &&
                massCenter.y >= y && massCenter.y < (y + length) &&
                massCenter.z >= z && massCenter.z < (z + length);
    }

    public void draw(CodeDraw cd) {
        if (Simulation.DRAW_OCTANTS) {
            cd.setColor(Color.WHITE);
            cd.setLineWidth(1);
            cd.drawSquare(cd.getWidth() * (x - Double.NEGATIVE_INFINITY) / (Double.NEGATIVE_INFINITY - Double.POSITIVE_INFINITY),
                    cd.getWidth() * y / Simulation.SECTION_SIZE,
                    cd.getWidth() * length / Simulation.SECTION_SIZE);
        }
    }

    @Override
    public String toString() {
        return "Octant{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", length=" + length +
                '}';
    }
}
