import codedraw.CodeDraw;

public class Octree {

    private static final int maxBodies = Simulation.MAX_BODIES_PER_OCTANT;
    private final Octant octant;
    private final Body[] bodies = new Body[maxBodies];
    private final Octree[] children = new Octree[8];
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
                    for (Body body : bodies) {
                        for (Octree child : children) {
                            if (body != null) {
                                child.add(body);
                            }
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

    public int size() {
        return size(this);
    }

    private int size(Octree tree) {
        int size = 0;
        if (tree.hasSubTrees()) {
            for (Octree octree : tree.children) {
                size += size(octree);
            }
        } else {
            size = tree.bodyCount();
        }
        return size;
    }

    private boolean hasSubTrees() {
        for (Octree octree : children) {
            if (octree == null) {
                return false;
            }
        }
        return true;
    }

    public void draw(CodeDraw cd) {
        for (Octree octree : children) {
            if (octree != null) {
                octree.draw(cd);
            } else {
                for (Body b : bodies) {
                    if (b != null) {
                        b.draw(cd);
                        octant.draw(cd);
                    }
                }
            }
        }
    }

    public double mass() {
        double mass = 0;
        if (hasSubTrees()) {
            for (Octree octree : children) {
                mass += octree.mass();
            }
        } else {
            for (Body body : bodies) {
                if (body != null) {
                    mass += body.mass();
                }
            }
        }
        return mass;
    }

    public Vector3 massCenter() {
        Vector3 result = new Vector3();
        if (hasSubTrees()) {
            for (Octree octree : children) {
                result = result.plus(octree.massCenter().times(octree.mass()));
            }
            result = result.times(1 / mass());
        } else {
            if (bodyCount() == 0) {
                result = new Vector3();
            } else {
                for (Body body : bodies) {
                    if (body != null) {
                        result = result.plus(body.massCenter().times(body.mass()));
                    }
                }
                result = result.times(1 / mass());
            }
        }

        return result;
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
        s += octant.toString() + "\n";
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                s += i + ": " + children[i].toString();
            }
        }
        return s;
    }
}
