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
                octant.setMass(this.mass());
                octant.setMassCenter(this.massCenter());
            } else {
                if (!isDivided) {
                    divide();
                    for (Body body : bodies) {
                        for (Octree child : children) {
                            if (body != null) {
                                child.add(body);
                            }
                            child.octant.setMass(child.mass());
                            child.octant.setMassCenter(child.massCenter());
                        }
                    }
                }
                for (Octree child : children) {
                    child.add(b);
                }
            }
        }
    }

    // adds all bodies of tree to this octree
    public void addAllBodies(Octree tree) {
        if (tree.hasSubTrees()) {
            for (Octree octree : tree.children) {
                octree.addAllBodies(octree);
            }
        } else {
            for (Body body : bodies) {
                if (body != null) {
                    this.add(body);
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

    // calculates the gravitational force exerted on every body in this octree
    public void calculateForce(Octree tree) {
        if (hasSubTrees()) {
            for (Octree octree : children) {
                octree.calculateForce(tree);
            }
        } else {
            for (Body body : bodies) {
                if (body != null) {
                    body.setGravitationalForce(tree.gravitationalForce(body));
                }
            }
        }
    }

    // calculates the gravitational force exerted by this octree on body b
    public Vector3 gravitationalForce(Body b) {

        if (hasSubTrees()) {
            for (Octree octree : children) {
                if (octree.octant.getLength() / (octree.octant.getMassCenter().distanceTo(b.massCenter())) < Simulation.T) {
                    Vector3 direction = octree.octant.getMassCenter().minus(b.massCenter());
                    double distance = direction.length();
                    direction.normalize();
                    double force = Simulation.G * (octree.octant.getMass() * b.mass()) / (distance * distance);
                    return direction.times(force);
                } else {
                    return octree.gravitationalForce(b);
                }
            }
        } else {
            for (Body body : bodies) {
                if (body != null) {
                    Vector3 direction = body.massCenter().minus(b.massCenter());
                    double distance = direction.length();
                    direction.normalize();
                    double force = Simulation.G * (body.mass() * b.mass()) / (distance * distance);
                    return direction.times(force);
                }
            }
        }
        return new Vector3();
    }


    // move all bodies in this octree according to the force exerted on them
    public void moveBodies() {
        if (hasSubTrees()) {
            for (Octree octree : children) {
                octree.moveBodies();
            }
        } else {
            for (Body body : bodies) {
                if (body != null) {
                    body.move(body.getGravitationalForce());
                }
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
        s += octant.toString() + "\n";
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                s += i + ": " + children[i].toString();
            }
        }
        return s;
    }
}
