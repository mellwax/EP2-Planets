import codedraw.CodeDraw;

public class Octree {

    private final Octant octant;
    private Body body;
    private final Octree[] children = new Octree[8];
    private boolean isDivided;

    public Octree(Octant octant) {
        this.octant = octant;
        isDivided = false;
    }

    // adds body b to this octree
    public void add(Body b) {
        if (octant.contains(b.massCenter())) {
            if (body == null && !isDivided) {
                body = b;
                octant.setMass(this.mass());
                octant.setMassCenter(this.massCenter());
            } else {
                if (!isDivided) {
                    divide();
                    for (Octree child : children) {
                        child.add(body);
                        child.octant.setMass(child.mass());
                        child.octant.setMassCenter(child.massCenter());
                    }
                    body = null;
                }
                for (Octree child : children) {
                    child.add(b);
                }
            }
        }
    }

    // adds body b to this octree and merges it if necessary
    public void addMerge(Body b) {
        if (octant.contains(b.massCenter())) {
            if (body == null && !isDivided) {
                body = b;
                octant.setMass(this.mass());
                octant.setMassCenter(this.massCenter());
            } else {
                if (!isDivided) {
                    if (body.distanceTo(b) < (body.radius() + b.radius())) {
                        body = body.merge(b);
                        octant.setMass(this.mass());
                        octant.setMassCenter(this.massCenter());
                    } else {
                        divide();
                        for (Octree child : children) {
                            child.addMerge(body);
                            child.octant.setMass(child.mass());
                            child.octant.setMassCenter(child.massCenter());
                        }
                        body = null;
                    }
                }
                if (isDivided) {
                    for (Octree child : children) {
                        child.addMerge(b);
                    }
                }
            }
        }
    }

    // adds all bodies of tree to this octree and merges them if necessary
    public void addMergeAllBodies(Octree tree) {
        if (tree.isDivided) {
            for (Octree child : tree.children) {
                this.addMergeAllBodies(child);
            }
        } else {
            if (tree.body != null) {
                this.addMerge(tree.body);
            }
        }
    }

    // adds all bodies of tree to this octree
    public void addAllBodies(Octree tree) {
        if (tree.isDivided) {
            for (Octree child : tree.children) {
                this.addAllBodies(child);
            }
        } else {
            if (tree.body != null) {
                this.add(tree.body);
            }
        }
    }

    // adds all bodies in bodies to this octree
    public void addFromArray(Body[] bodies) {
        for (Body body : bodies) {
            this.add(body);
        }
    }

    // parts the octant of this in 8 more
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

    // draws all bodies of this octree
    public void draw(CodeDraw cd) {
        if (isDivided) {
            for (Octree child : children) {
                child.draw(cd);
            }
        } else {
            if (body != null) {
                body.draw(cd);
                octant.draw(cd);
            }
        }
    }

    // returns the overall mass of this octree
    public double mass() {
        double mass = 0;
        if (isDivided) {
            for (Octree child : children) {
                mass += child.mass();
            }
        } else {
            if (body != null) {
                mass += body.mass();
            }
        }
        return mass;
    }

    // returns the mass center of this octree
    public Vector3 massCenter() {
        Vector3 result = new Vector3();
        if (isDivided) {
            for (Octree child : children) {
                result = result.plus(child.massCenter().times(child.mass()));
            }
            result = result.times(1 / mass());
        } else {
            if (body != null) {
                result = result.plus(body.massCenter().times(body.mass()));
                result = result.times(1 / mass());
            }
        }
        return result;
    }

    // calculates the gravitational force exerted on every body in this octree
    public void calculateForce(Octree tree) {
        if (isDivided) {
            for (Octree child : children) {
                child.calculateForce(tree);
            }
        } else {
            if (body != null) {
                body.setGravitationalForce(tree.gravitationalForce(body));
            }
        }
    }

    // calculates the gravitational force exerted by this octree on body b
    public Vector3 gravitationalForce(Body b) {

        if (isDivided) {
            Vector3 forceSum = new Vector3();
            for (Octree child : children) {
                if (child.octant.getLength() / (b.massCenter().distanceTo(child.octant.getMassCenter())) < Simulation.T) {
                    Vector3 direction = child.octant.getMassCenter().minus(b.massCenter());
                    double distance = direction.length();
                    direction.normalize();
                    double force = Simulation.G * (child.octant.getMass() * b.mass()) / (distance * distance);
                    forceSum = forceSum.plus(direction.times(force));
                } else {
                    forceSum = forceSum.plus(child.gravitationalForce(b));
                }
            }
            return forceSum;
        } else {
            if (body != null && !body.equals(b)) {
                b.gravitationalForce(body);
            }
        }
        return new Vector3();
    }


    // move all bodies in this octree according to the force exerted on them
    public void moveBodies() {
        if (isDivided) {
            for (Octree child : children) {
                child.moveBodies();
            }
        } else {
            if (body != null) {
                body.move(body.getGravitationalForce());
            }
        }
    }

    public void simulate() {
        resetForces();
        calculateForce(this);
        moveBodies();
    }

    public void resetForces() {
        if (isDivided) {
            for (Octree child : children) {
                child.resetForces();
            }
        } else {
            if (body != null) {
                body.setGravitationalForce(new Vector3());
            }
        }
    }

    // returns the number of bodies this octree contains
    public int size(Octree tree) {
        int size = 0;
        if (tree.isDivided) {
            for (Octree child : tree.children) {
                size += size(child);
            }
        } else {
            if (tree.body != null) {
                size = 1;
            }
        }
        return size;
    }

    @Override
    public String toString() {
        String s = "Bodies: ";
        if (body != null) {
            s += body + " ";
        } else {
            s += "null ";
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
