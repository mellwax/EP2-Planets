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
    public static final double SECTION_SIZE = 5 * AU; // the size of the square region in space
    public static final int NUMBER_OF_BODIES = 1000;
    public static final double OVERALL_SYSTEM_MASS = NUMBER_OF_BODIES * SUN_MASS; // kilograms
    public static final boolean DRAW_OCTANTS = false;
    public static final int MAX_BODIES_PER_OCTANT = 1;

    // threshold
    public static final int T = 1;

    public static void main(String[] args) {
        CodeDraw cd = new CodeDraw();
        cd.clear(Color.BLACK);

        Octant octant = new Octant(-SECTION_SIZE/2, -SECTION_SIZE/2, -SECTION_SIZE/2, SECTION_SIZE);
        Octree octree = new Octree(octant);
        Random random = new Random(2022);

        for (int i = 0; i < NUMBER_OF_BODIES; i++) {

            double mass = Math.abs(random.nextGaussian()) * OVERALL_SYSTEM_MASS / NUMBER_OF_BODIES; // kg

            double massCenterX = 0.3 * random.nextGaussian() * SECTION_SIZE/2;
            double massCenterY = 0.3 * random.nextGaussian() * SECTION_SIZE/2;
            double massCenterZ = 0.3 * random.nextGaussian() * SECTION_SIZE/2;

            double currentMovementX = 0 + random.nextGaussian() * 5e5;
            double currentMovementY = 0 + random.nextGaussian() * 5e5;
            double currentMovementZ = 0 + random.nextGaussian() * 5e5;

            Vector3 massCenter = new Vector3(massCenterX, massCenterY, massCenterZ);
            Vector3 currentMovement = new Vector3(currentMovementX, currentMovementY, currentMovementZ);

            octree.add(new Body(mass, massCenter, currentMovement));

        }


        double seconds = 0;

        // simulation loop
        while (true) {

            seconds++; // each iteration computes the movement of the celestial bodies within one second.
            //System.out.println(seconds);

            octree.calculateForce(octree);
            octree.moveBodies();
            //octree.printOctants();

            // show all movements in the canvas only every hour (to speed up the simulation)
            if (seconds % (360) == 0) {
                // clear old positions (exclude the following line if you want to draw orbits).
                cd.clear(Color.BLACK);
                octree.draw(cd);
                System.out.println("Image: " + seconds/360);
                // show new positions
                cd.show();
            }

            Octree newOctree = new Octree(octant);
            newOctree.addAllBodies(octree);
            octree = newOctree;
        }

    }
}
