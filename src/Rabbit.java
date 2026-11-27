import java.awt.*;
import java.sql.Array;
import java.util.*;
import java.util.List;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.*;
import itumulator.world.*;

public class Rabbit extends Animal implements DynamicDisplayInformationProvider {
    private boolean inBurrow;
    private Burrow burrow;

    Rabbit(int energy, int health, int age, boolean inBurrow, Burrow burrow) {
        super(energy, health, age);
        this.inBurrow = inBurrow;
        this.burrow = burrow;
        foodType.add(FoodType.plant);
        largeAnimal = false;
    }

    public Rabbit createOffspring() {
        return new Rabbit(5, 3, 0, true, burrow);
    }

    public void onAnimalDeath(World world) {
        if (burrow != null) {
            burrow.removeDenizen(this);
            burrow.rabbitLeaving(this);
        }
    }

    public Location getLocation(World world) {
        return !inBurrow && world.contains(this) ? world.getLocation(this) : burrow.burrowLocation();
    }

    public void act(World world) {
        super.act(world);
        if (dead) return;
        if (world.isDay()) dayLogic(world);
        else if (world.isNight()) nightLogic(world);
    }

    @Override
    public DisplayInformation getInformation() {
        if (age >= 2) {
            return new DisplayInformation(Color.blue, "rabbit-large");
        }
        return new DisplayInformation(Color.blue, "rabbit-small");
    }

    private void dayLogic(World world) {
        //leaves it's burrow if the rabbit is in one
        if (inBurrow) {
            this.leaveBurrow(world);
            if (inBurrow) return;
            if (!burrow.isDenizen(this)) {
                burrow = null;
            }
        }

        //Ensures rabbit is in the world
        if (!world.contains(this)) return;

        for (int currentEnergy = energy == 0 ? 1 : energy; currentEnergy > 0; currentEnergy--) {
            boolean ate = false;
            if (!this.hasEaten) ate = findAndEatFood(world, world.getEmptySurroundingTiles());
            if (!ate) randomMovement(world);
        }
    }

    private void nightLogic(World world) {
        if (!inBurrow && burrow != null) { //Pathfind towards Burrow
            Location direction = pathfindToBurrow(world);
            Object potentialObstacle = world.getTile(direction);
            if (potentialObstacle == null || potentialObstacle instanceof NonBlocking) {
                world.move(this, direction);
            }
        } else if (burrow == null) { //Find or create Burrow
            searchForAndJoinBurrow(world, 3);
            if (burrow == null) this.digBurrow(world);
        }

        //enters a burrow if the rabbit is on top of one and its night
        if (!inBurrow && burrow != null) {
            Set<Location> surroundingTiles = world.getSurroundingTiles();
            for (Location location : surroundingTiles) {
                if (world.getNonBlocking(location) == burrow) {
                    this.enterBurrow(world);
                    break;
                }
            }
        } else if (inBurrow) { //Rabbit breeds in Burrow
            if (burrow.reproductionAvailable()) {
                Rabbit baby = this.createOffspring();
                world.add(baby);
                burrow.addDenizen(baby);
                burrow.rabbitEntering(baby);
            }
        }
    }

    private boolean findAndEatFood(World world, Set<Location> neighbours) {
        for (Location neighbour : neighbours) {
            if (world.containsNonBlocking(neighbour) && world.getTile(neighbour) instanceof NonBlocking) {
                world.move(this, neighbour);
                if (world.getNonBlocking(neighbour) instanceof Structure structure && foodType.contains(structure.getFoodType())) {
                    structure.damageStructure(1, world);
                    this.eat();
                    return true;
                }
            }
        }
        return false;
    }

    private Location pathfindToBurrow(World world) {
        Location rabbitLocation = getLocation(world);
        Location burrowLocation = burrow.burrowLocation();
        int xDiff = burrowLocation.getX() - rabbitLocation.getX();
        int yDiff = burrowLocation.getY() - rabbitLocation.getY();
        int xMovement = 0;
        int yMovement = 0;

        if (xDiff != 0) {
            xMovement = xDiff < 0 ? -1 : 1;
        }

        if (yDiff != 0) {
            yMovement = yDiff < 0 ? -1 : 1;
        }

        Location direction = new Location(rabbitLocation.getX() + xMovement, rabbitLocation.getY() + yMovement);

        Object obstacle = world.getTile(direction);
        if (obstacle != null && !(obstacle instanceof NonBlocking)) {
            Set<Location> nearbyBlocker = world.getSurroundingTiles(direction);
            Set<Location> nearbyRabbit = world.getSurroundingTiles(rabbitLocation);
            Set<Location> intersection = new HashSet<>(nearbyBlocker);
            intersection.retainAll(nearbyRabbit);

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

    public void searchForAndJoinBurrow(World world, int searchRadius) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(searchRadius);
        for (Location location : surroundingTiles) {
            if (world.getNonBlocking(location) instanceof Burrow foundBurrow && !foundBurrow.atFullCapacity()) {
                burrow = foundBurrow;
                burrow.addDenizen(this);
                break;
            }
        }
    }

    //code to check if the rabbit can dig a burrow, then it digs one
    public void digBurrow(World world) {
        Location currentLocation = world.getLocation(this);
        Object potentialObstacle = world.getNonBlocking(currentLocation);
        if (!(potentialObstacle instanceof Burrow)) {
            burrow = new Burrow(3, 3, currentLocation.getX(), currentLocation.getY());
            burrow.addDenizen(this);
            if (potentialObstacle != null) world.delete(potentialObstacle);
            world.add(burrow);
            world.setTile(currentLocation, burrow);
        }
    }

    //code for when the rabbit enters a burrow
    public void enterBurrow(World world) {
        world.remove(this);
        inBurrow = true;
        burrow.rabbitEntering(this);
    }

    //code for when the rabbit leaves a burrow
    public void leaveBurrow(World world) {
        List<Location> emptySurroundings = new ArrayList<>(world.getEmptySurroundingTiles(burrow.burrowLocation()));
        if (emptySurroundings.isEmpty()) return;
        Location targetLocation = emptySurroundings.get(r.nextInt(0, emptySurroundings.size()));
        world.setTile(targetLocation, this);
        world.setCurrentLocation(targetLocation);
        inBurrow = false;
        burrow.rabbitLeaving(this);
    }

    //get the rabbits max energy
    public int getEnergy() {
        return energy;
    }

    //get the rabbits health
    public int getHealth() {
        return health;
    }
}