import codedraw.CodeDraw;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Simulation {

    // gravitational constant
    public static final double G = 6.6743e-11;

    // one astronomical unit (AU) is the average distance of earth to the sun.
    public static final double AU = 150e9; // meters

    // some further constants needed in the simulation
    public static final double SUN_MASS = 1.989e30; // kilograms
    public static final double SUN_RADIUS = 696340e3; // meters

    // set some system parameters
    public static final double SECTION_SIZE = 2 * AU; // the size of the square region in space
    public static final int NUMBER_OF_BODIES = 10000;
    public static final double OVERALL_SYSTEM_MASS = NUMBER_OF_BODIES * SUN_MASS; // kilograms

    // threshold
    public static final int T = 1;

    // change visual output for testing
    public static final boolean DRAW_OCTANTS = true;
    public static boolean DRAW_3D = false;

    // merge bodies
    public static boolean MERGE_BODIES = false;

    public static void main(String[] args) {

        CodeDraw cd = new CodeDraw();
        cd.clear(Color.BLACK);

        Octant octant = new Octant(-SECTION_SIZE / 2, -SECTION_SIZE / 2, -SECTION_SIZE / 2, SECTION_SIZE);
        Octree octree = new Octree(octant);
        Random random = new Random(2022);

        // add bodies to octree
        for (int i = 0; i < NUMBER_OF_BODIES; i++) {

            double mass = Math.abs(random.nextGaussian()) * OVERALL_SYSTEM_MASS / NUMBER_OF_BODIES; // kg

            double massCenterX;
            double massCenterY;
            double massCenterZ;

            if (i < NUMBER_OF_BODIES / 2) {
                massCenterX = 0.2 * random.nextGaussian() * SECTION_SIZE / 4;
                massCenterY = 0.2 * random.nextGaussian() * SECTION_SIZE / 4;
                massCenterZ = DRAW_3D ? 0.2 * random.nextGaussian() * SECTION_SIZE / 4 : 0;
            } else {
                massCenterX = 0.2 * random.nextGaussian() * SECTION_SIZE * 3/4 ;
                massCenterY = 0.2 * random.nextGaussian() * SECTION_SIZE * 3/4;
                massCenterZ = DRAW_3D ? 0.2 * random.nextGaussian() * SECTION_SIZE * 3/4 : 0;
            }

            double currentMovementX = 0 + random.nextGaussian() * 5e7;
            double currentMovementY = 0 + random.nextGaussian() * 5e7;
            double currentMovementZ = DRAW_3D ? 0 + random.nextGaussian() * 5e7 : 0;

            Vector3 massCenter = new Vector3(massCenterX, massCenterY, massCenterZ);
            Vector3 currentMovement = new Vector3(currentMovementX, currentMovementY, currentMovementZ);
            Body body = new Body(mass, massCenter, currentMovement);

            if (MERGE_BODIES) {
                octree.addMerge(body);
            } else {
                octree.add(body);
            }
        }

        Body blackHole = new Body(100 * SUN_MASS, new Vector3(), new Vector3());
        octree.add(blackHole);

        double seconds = 0;

        // simulation loop
        while (true) {



            seconds++; // each iteration computes the movement of the celestial bodies within one second.

            long startTime = System.nanoTime();

            // calculate gravitational force for every body in octree and move them according to it
            octree.simulate();

            // print duration of each cycle
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            duration = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS);
            System.out.println("Cycle finished in: " + duration + " ms");

            // update octree
            Octree newOctree = new Octree(octant);
            if (MERGE_BODIES) {
                newOctree.addMergeAllBodies(octree);
            } else {
                newOctree.addAllBodies(octree);
            }
            octree = newOctree;



            // show all movements in the canvas
            cd.clear(Color.BLACK);
            octree.draw(cd);
            // show new positions
            cd.show();

            // print number of image
            if (seconds % 10 == 0) {
                System.out.println("Image: " + (int)seconds);
            }




        }
    }
}
