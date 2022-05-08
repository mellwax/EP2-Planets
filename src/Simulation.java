import codedraw.CodeDraw;

import java.awt.*;
import java.util.Random;

public class Simulation {

    // gravitational constant
    public static final double G = 6.6743e-11;

    // one astronomical unit (AU) is the average distance of earth to the sun.
    public static final double AU = 150e9; // meters

    // some further constants needed in the simulation
    public static final double SUN_MASS = 1.989e30; // kilograms
    public static final double SUN_RADIUS = 696340e3; // meters

    // set some system parameters
    public static final double SECTION_SIZE = 10 * AU; // the size of the square region in space
    public static final int NUMBER_OF_BODIES = 21;
    public static final double OVERALL_SYSTEM_MASS = 20 * SUN_MASS; // kilograms
    public static final boolean DRAW_OCTANTS = true;
    public static final int MAX_BODIES_PER_OCTANT = 1;

    public static void main(String[] args) {
        CodeDraw cd = new CodeDraw();
        cd.clear(Color.BLACK);

        Octant octant = new Octant(0, 0, 0, 600);
        Octree octree = new Octree(octant);
        Random random = new Random(777);

        for (int i = 0; i < NUMBER_OF_BODIES; i++) {
            Body b = new Body(random.nextDouble(600),
                    new Vector3(random.nextDouble(600), random.nextDouble(600), random.nextDouble(600)),
                    new Vector3(random.nextDouble(100), random.nextDouble(100), random.nextDouble(100)));
            octree.add(b);
            System.out.println(b);
        }
        System.out.println(octree);
        System.out.println(octree.size());
        octree.draw(cd);
        cd.show();
    }
}
