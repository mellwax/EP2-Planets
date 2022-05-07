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
    public static final int MAX_BODIES = 1;

    public static void main(String[] args) {
        CodeDraw cd = new CodeDraw();
        cd.clear(Color.BLACK);
        Octant octant = new Octant(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        Octant octant1 = new Octant(cd.getWidth() / 2.0,0, 0,SECTION_SIZE / 16.0);

        Octree octree = new Octree(octant);
        Random random = new Random(777);
        for (int i = 0; i < NUMBER_OF_BODIES; i++) {
            int b = random.nextInt(11);
            System.out.println(random.nextDouble() * Math.pow(10, b));
        }

        for (int i = 0; i < NUMBER_OF_BODIES; i++) {
            /*Body b = new Body(Math.abs(random.nextGaussian() * OVERALL_SYSTEM_MASS / NUMBER_OF_BODIES),
                    new Vector3(random.nextDouble() * Math.pow(10, random.nextInt(11)),
                            random.nextDouble() * Math.pow(10, random.nextInt(11)),
                            random.nextDouble() * Math.pow(10, random.nextInt(11))),
                    new Vector3(random.nextDouble() * Math.pow(10, random.nextInt(11)),
                            random.nextDouble() * Math.pow(10, random.nextInt(11)),
                            random.nextDouble() * Math.pow(10, random.nextInt(11))));
                            */

            Body b = new Body(Math.abs(random.nextGaussian()) * OVERALL_SYSTEM_MASS / NUMBER_OF_BODIES,
                    new Vector3(0.5 * random.nextGaussian() * AU, 0.5 * random.nextGaussian() * AU, 0.5 * random.nextGaussian() * AU),
                    new Vector3(0 + random.nextGaussian() * 5e3, 0 + random.nextGaussian() * 5e3, 0 + random.nextGaussian() * 5e3));
            octree.add(b);
            //b.draw(cd);
            System.out.println(i);
        }
        System.out.println(octree.toString());
        octree.draw(cd);
        cd.show();
    }
}
