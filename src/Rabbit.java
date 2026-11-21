import java.util.*;
import itumulator.simulator.*;
import itumulator.world.*;

public class Rabbit extends Animal {
    Location burrowLocation;
    Location rabbitLocation;
    boolean inBurrow = false;
    Burrow burrow;

    Rabbit(int energy, int health, int age) {
        super(energy, health, age);
        foodType.add(FoodType.plant);
    }

    public void act(World world) {
        //rabbits random movement
        Set<Location> neighbours = world.getEmptySurroundingTiles();
        List<Location> list;
        Random r;
        Location l;
        rabbitLocation = world.getLocation(this);
        for (int currentEnergy = energy; currentEnergy > 0; currentEnergy--) {
            neighbours = world.getEmptySurroundingTiles();
            list = new ArrayList<>(neighbours);
            r = new Random();
            l = list.get(r.nextInt(0,list.size()));
            world.move(this, l);
        }

        //rabbits movement towards burrow at night
        if (world.isNight()) {
            if (burrowLocation != null) {
                for (int currentEnergy = energy; currentEnergy > 0; currentEnergy--) {
                    rabbitLocation = world.getLocation(this);
                    int xDiff = burrowLocation.getX() - rabbitLocation.getX();
                    int yDiff = burrowLocation.getY() - rabbitLocation.getY();
                    int xMovement = 0;
                    int yMovement = 0;

                    if (xDiff != 0) {
                        xMovement = xDiff < 0 ? -1: 1;
                    }

                    if (yDiff != 0) {
                        yMovement = yDiff < 0 ? -1: 1;
                    }

                    Location direction = new Location(rabbitLocation.getX() + xMovement, rabbitLocation.getY() + yMovement);

                    if (world.getTile(direction) != null) {
                        Set<Location> nearbyBlocker = world.getSurroundingTiles(direction);
                        Set<Location> nearbyRabbit = world.getSurroundingTiles(rabbitLocation);
                        Set<Location> intersection = new HashSet<>(nearbyBlocker);
                        intersection.retainAll(nearbyRabbit);

                        for (Location tile : intersection) {
                            if (world.getTile(tile) == null) {
                                world.move(this,tile);
                                break;
                            }
                        }
                    }
                    world.move(this, direction);
                }
            }

            if (burrowLocation == null && world.contains(burrow)) {
                Location bLocation = world.getLocation(burrow);
                for (int currentEnergy = energy; currentEnergy > 0; currentEnergy--) {
                    rabbitLocation = world.getLocation(this);
                    int xDiff = bLocation.getX() - rabbitLocation.getX();
                    int yDiff = bLocation.getY() - rabbitLocation.getY();
                    int xMovement = 0;
                    int yMovement = 0;

                    if (xDiff != 0) {
                        xMovement = xDiff < 0 ? -1 : 1;
                    }

                    if (yDiff != 0) {
                        yMovement = yDiff < 0 ? -1 : 1;
                    }

                    Location direction = new Location(rabbitLocation.getX() + xMovement, rabbitLocation.getY() + yMovement);

                    if (world.getTile(direction) != null) {
                        Set<Location> nearbyBlocker = world.getSurroundingTiles(direction);
                        Set<Location> nearbyRabbit = world.getSurroundingTiles(rabbitLocation);
                        Set<Location> intersection = new HashSet<>(nearbyBlocker);
                        intersection.retainAll(nearbyRabbit);

                        for (Location tile : intersection) {
                            if (world.getTile(tile) == null) {
                                world.move(this, tile);
                                break;
                            }
                        }
                    }
                    world.move(this, direction);
                }
            } else if (burrowLocation == null) {
                this.digBurrow(world);
            }
        }

        //enters a burrow if the rabbit is on top of one and its night
        if (world.isNight() && world.getTile(rabbitLocation) instanceof Burrow) {
            this.enterBurrow(world);
        }

        //leaves a burrow if the rabbit is in one and its day
        if (inBurrow && world.isDay()) {
            this.leaveBurrow(world);
        }

        //does damage to the rabbit if it hasn't eaten for a day
        if (!hasEaten && world.isNight()) {
            this.starve();
        }

        //resets hasEaten so a rabbit can eat again
        if (hasEaten && world.isNight()) {
            this.setHasEaten();
        }

        //adds age to the rabbit every night
        if (world.isNight()) {
            this.ageUp();
        }

        //make the rabbit eat
        for (Location neighbour : neighbours) {
            if (world.containsNonBlocking(neighbour) && !hasEaten) {
                world.move(this, neighbour);
                if (world.getTile(neighbour) instanceof Structure) {
                    Structure structure = (Structure) world.getTile(neighbour);
                    if (foodType.contains(structure.getFoodType())) {
                        structure.damageStructure(1);
                        this.eat();
                        break;
                    }
                }
            }
        }

        if (inBurrow) {
            Burrow currentBurrow = (Burrow) world.getTile(rabbitLocation);
            if (!currentBurrow.isFull()  && currentBurrow.rabbitAmount() >= 2) {
                Rabbit baby = this.createOffspring();
                world.add(baby);
                currentBurrow.rabbitEntering(baby);
            }
        }

    }

    //code to check if the rabbit can dig a burrow, then it digs one
    public void digBurrow(World world) {
        Location currentLocation = world.getLocation(this);
        if (world.containsNonBlocking(currentLocation)) {
            burrow = new Burrow(3, 3, currentLocation.getX(), currentLocation.getY());
            world.add(burrow);
            world.setTile(currentLocation, burrow);
            burrowLocation = world.getLocation(burrow);
        }
    }

    //code for when the rabbit enters a burrow
    public void enterBurrow(World world) {
        world.remove(this);
        inBurrow = true;
    }

    //code for when the rabbit leaves a burrow
    public void leaveBurrow(World world) {
        Location currentLocation = world.getLocation(this);
        world.setTile(currentLocation, this);
        inBurrow = false;
    }

    //get the rabbits max energy
    public int getEnergy() {
        return energy;
    }

    //get the rabbits health
    public int getHealth() {
        return health;
    }

    @Override
    public Rabbit createOffspring() {
        return new Rabbit(3,3,0);
    }
}
