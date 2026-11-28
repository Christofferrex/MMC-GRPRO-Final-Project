import itumulator.simulator.*;
import itumulator.world.*;
import java.util.*;

public abstract class Animal implements Actor {
    protected int energy;
    protected int health;
    protected int age;
    protected int timeInWorld;
    protected boolean hasEaten = false;
    protected boolean largeAnimal;
    protected List<FoodType> foodType;
    protected final Random r;

    protected boolean dead = false;
    protected boolean reproducedTonight = false;

    //constructor
    Animal(int energy, int health, int age) {
        this.energy = energy;
        this.health = health;
        this.age = age;
        timeInWorld = 0;
        foodType = new ArrayList<>();
        r = new Random();
    }

    //abstract method for breeding
    public abstract Object createOffspring();

    public abstract void onAnimalDeath(World world);

    public void newDay(World world) {
        if (!this.hasEaten) {
            this.starve();
        }
        this.ageUp();
        this.resetHasEaten();
    }

    public void newNight(World world) {
        reproducedTonight = false;
    }

    public abstract Location getLocation(World world);

    //adds age
    public void ageUp() {
        age++;
        if (this.age % 2 == 0) {
            this.energyDown();
        }
    }

    //removes energy
    public void energyDown() {
        energy--;
    }

    //sets hasEaten to true and increases health
    public void eat() {
        hasEaten = true;
        health++;
    }

    //removes health
    public void starve() {
        health--;
    }

    //sets hasEaten to false
    public void resetHasEaten() {
        hasEaten = false;
    }

    protected void randomMovement(World world) {
        Set<Location> neighbours = world.getEmptySurroundingTiles(getLocation(world));
        List<Location> listOfNeighbours = new ArrayList<>(neighbours);
        if (!listOfNeighbours.isEmpty() && world.contains(this)) {
            world.move(this, listOfNeighbours.get(r.nextInt(0, listOfNeighbours.size())));
        }
    }

    @Override
    public void act(World world) {
        timeInWorld++;

        if (timeInWorld % 20 == 0) newDay(world);
        else if (timeInWorld % 10 == 0) newNight(world);

        if (health <= 0 || energy <= 0) {
            onAnimalDeath(world);
            dead = true;

            Location animalLocation = getLocation(world);
            world.delete(this);

            if (world.isTileEmpty(animalLocation)) {
                Carcass carcass = new Carcass(largeAnimal ? 5 : 3, largeAnimal);
                world.add(carcass);
                world.setTile(animalLocation, carcass);
            }
        }
    }

    //taking damage
    public void takeDamage(int damage) {
        health -= damage;
    }

    public void setReproducedTonight(boolean value) {
        reproducedTonight = value;
    }

    public boolean getReproducedTonight() {
        return reproducedTonight;
    }

    protected void moveTowards(World world, Location targetLocation) {
        Location direction = pathfindToLocation(world, targetLocation);
        Object potentialObstacle = world.getTile(direction);
        if (potentialObstacle == null || potentialObstacle instanceof NonBlocking) {
            world.move(this, direction);
        }
    }

    protected Location pathfindToLocation(World world, Location targetLocation) {
        Location currentLocation = getLocation(world);
        int xDiff = targetLocation.getX() - currentLocation.getX();
        int yDiff = targetLocation.getY() - currentLocation.getY();
        int xMovement = 0;
        int yMovement = 0;

        if (xDiff != 0) {
            xMovement = xDiff < 0 ? -1 : 1;
        }

        if (yDiff != 0) {
            yMovement = yDiff < 0 ? -1 : 1;
        }

        Location direction = new Location(currentLocation.getX() + xMovement, currentLocation.getY() + yMovement);

        Object obstacle = world.getTile(direction);
        if (obstacle != null && !(obstacle instanceof NonBlocking)) {
            Set<Location> nearbyBlocker = world.getSurroundingTiles(direction);
            Set<Location> nearbyAnimal = world.getSurroundingTiles(currentLocation);
            Set<Location> intersection = new HashSet<>(nearbyBlocker);
            intersection.retainAll(nearbyAnimal);

            List<Location> intersectionList = new ArrayList<>(intersection);

            int loopAmount = intersectionList.size();
            for (int i = 0; i < loopAmount; i++) {
                Location location = intersectionList.get(r.nextInt(0, intersectionList.size()));
                obstacle = world.getTile(location);
                if (obstacle == null || obstacle instanceof NonBlocking) {
                    return location;
                }
                intersectionList.remove(location);
            }
        }

        return direction;
    }
}
