import java.util.*;
import itumulator.simulator.*;
import itumulator.world.*;

public class Rabbit extends Animal {
    private boolean inBurrow;
    private Burrow burrow;
    private int timeInWorld;
    boolean newDay = false;

    Rabbit(int energy, int health, int age, boolean inBurrow, Burrow burrow) {
        super(energy, health, age);
        this.inBurrow = inBurrow;
        this.burrow = burrow;
        foodType.add(FoodType.plant);
    }

    public void act(World world) {
        //rabbits random movement
        Set<Location> neighbours;
        Location rabbitLocation = world.isOnTile(this) ? world.getLocation(this) : burrow.burrowLocation();

        if (timeInWorld % 20 == 0 && !newDay) {
            if (!this.hasEaten) {
                this.starve();
            }
            this.ageUp();
            this.setHasEaten();
            newDay = true;
        }

        if (health <= 0 || energy <= 0) {
            world.delete(this);
            if (burrow != null) burrow.removeDenizen(this);
            Carcass rabbitCarcass = new Carcass(3, false);
            world.add(rabbitCarcass);
            world.setTile(rabbitLocation, rabbitCarcass);
        }

        if (world.isDay()) {
            //leaves a burrow if the rabbit is in one and its day
            if (inBurrow) {
                this.leaveBurrow(world);
                if (inBurrow) return;
                if (!burrow.isDenizen(this)) {
                    burrow = null;
                }
            }

            if (!world.contains(this)) return;

            for (int currentEnergy = energy; currentEnergy > 0; currentEnergy--) {
                neighbours = world.getEmptySurroundingTiles();
                boolean ate = false;

                //make the rabbit eat
                if (!this.hasEaten) {
                    for (Location neighbour : neighbours) {
                        if (world.containsNonBlocking(neighbour) && world.getTile(neighbour) instanceof NonBlocking) {
                            world.move(this, neighbour);
                            if (world.getNonBlocking(neighbour) instanceof Structure structure && foodType.contains(structure.getFoodType())) {
                                structure.damageStructure(1, world);
                                this.eat();
                                ate = true;
                                break;
                            }
                        }
                    }
                }

                if (!ate) {
                    List<Location> listOfNeighbours = new ArrayList<>(neighbours);
                    if (!listOfNeighbours.isEmpty() && world.contains(this)) {
                        world.move(this, listOfNeighbours.get(r.nextInt(0, listOfNeighbours.size())));
                    }
                }
            }
        }

        //rabbits movement towards burrow at night
        if (world.isNight()) {
            if (!inBurrow && burrow != null) {
                rabbitLocation = world.getLocation(this);
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

                    for (Location tile : intersection) {
                        if (world.getTile(tile) == null) {
                            direction = tile;
                            break;
                        }
                    }
                }

                obstacle = world.getTile(direction);
                if (obstacle == null || obstacle instanceof NonBlocking) {
                    world.move(this, direction);
                }
            } else if (burrow == null) {
                Set<Location> surroundingTiles = world.getSurroundingTiles(3);
                for (Location location : surroundingTiles) {
                    if (world.getNonBlocking(location) instanceof Burrow foundBurrow && !foundBurrow.atFullCapacity()) {
                        burrow = foundBurrow;
                        break;
                    }
                }

                if (burrow == null) this.digBurrow(world);
            }

            //enters a burrow if the rabbit is on top of one and its night
            if (!inBurrow && burrow != null && world.getNonBlocking(rabbitLocation) == burrow) {
                this.enterBurrow(world);
            } else if (inBurrow) {
                if (!burrow.isFull() && burrow.rabbitAmount() >= 2) {
                    Rabbit baby = this.createOffspring();
                    world.add(baby);
                    burrow.addDenizen(baby);
                    burrow.rabbitEntering(baby);
                }
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

        if (!this.hasEaten) {
            starve();
        }
    }

    //code for when the rabbit leaves a burrow
    public void leaveBurrow(World world) {
        this.ageUp();
        this.setHasEaten();

        if (world.getTile(burrow.burrowLocation()) != burrow) return;
        world.setTile(burrow.burrowLocation(), this);
        world.setCurrentLocation(burrow.burrowLocation());
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

    @Override
    public Rabbit createOffspring() {
        return new Rabbit(5, 3, 0, true, burrow);
    }
}