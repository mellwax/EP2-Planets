import codedraw.CodeDraw;

import java.awt.*;

public class Octant {

    public double x;
    public double y;
    public double z;
    public double length;

    public Octant(double x, double y, double z, double length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = length;
    }

    public void draw(CodeDraw cd) {
        cd.setColor(Color.WHITE);
        cd.setLineWidth(1);
        cd.drawSquare(x, y, cd.getWidth() / length);
    }
}
