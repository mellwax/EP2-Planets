import codedraw.CodeDraw;

import java.util.Arrays;

public class Octree {

    private static final int maxBodies = Simulation.MAX_BODIES;
    private final Octant octant;
    private Body[] bodies = new Body[maxBodies];
    private Octree[] children = new Octree[8];
    private boolean isDivided;

    public Octree(Octant octant) {
        this.octant = octant;
        isDivided = false;
    }

    public void add(Body b) {
        if (octant.contains(b.massCenter())) {
            if (bodyCount() < maxBodies) {
                for (int i = 0; i < bodies.length; i++) {
                    if (bodies[i] == null) {
                        bodies[i] = b;
                    }
                }
            } else {
                if (!isDivided) {
                    divide();
                }
                for (int i = 0; i < bodies.length; i++) {
                    for (int j = 0; j < children.length; j++) {
                        if (bodies[i] != null) {
                            children[i].add(bodies[i]);
                            bodies[i] = null;
                        }
                    }
                }
                for (Octree child : children) {
                    child.add(b);
                }
            }
        }
    }

    public void divide() {
        children[0] = new Octree(new Octant(octant.getX(), octant.getY(), octant.getZ(), octant.getLength() / 2));
        children[1] = new Octree(new Octant(octant.getX() + octant.getLength() / 2, octant.getY(), octant.getZ(), octant.getLength() / 2));
        children[2] = new Octree(new Octant(octant.getX(), octant.getY() + octant.getLength() / 2, octant.getZ(), octant.getLength() / 2));
        children[3] = new Octree(new Octant(octant.getX() + octant.getLength() / 2, octant.getY() + octant.getLength() / 2, octant.getZ(), octant.getLength() / 2));
        children[4] = new Octree(new Octant(octant.getX(), octant.getY(), octant.getZ() + octant.getLength() / 2, octant.getLength() / 2));
        children[5] = new Octree(new Octant(octant.getX() + octant.getLength() / 2, octant.getY(), octant.getZ() + octant.getLength() / 2, octant.getLength() / 2));
        children[6] = new Octree(new Octant(octant.getX(), octant.getY() + octant.getLength() / 2, octant.getZ() + octant.getLength() / 2, octant.getLength() / 2));
        children[7] = new Octree(new Octant(octant.getX() + octant.getLength() / 2, octant.getY() + octant.getLength() / 2, octant.getZ() + octant.getLength() / 2, octant.getLength() / 2));
        isDivided = true;
    }

    private int bodyCount() {
        int count = 0;
        for (Body body : bodies) {
            if (body != null) {
                count++;
            }
        }
        return count;
    }

    public void draw(CodeDraw cd) {
        if (!isDivided) {
            for (int i = 0; i < bodies.length; i++) {
                if (bodies[i] != null) {
                    bodies[i].draw(cd);
                    octant.draw(cd);
                }
            }
        }
        for (Octree child : children) {
            if (child != null) {
                child.draw(cd);
            }
        }
    }

    @Override
    public String toString() {
        String s = "Bodies: ";
        for (int i = 0; i < bodies.length; i++) {
            if (bodies[i] != null) {
                s += bodies[i].toString() + " ";
            } else {
                s += "null ";
            }
        }
        s += octant.toString();
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                s += children[i].toString();
            }
        }
        return s + "\n";
    }
}
