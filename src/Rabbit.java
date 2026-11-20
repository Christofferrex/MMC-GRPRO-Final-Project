import java.util.*;
import itumulator.simulator.*;
import itumulator.world.*;

public class Rabbit extends Animal {
    Location burrowLocation;
    Location rabbitLocation;
    boolean inBurrow = false;

    Rabbit(int energy, int health, int age) {
        super(energy, health, age);
        foodType.add(FoodType.plant);
    }

    public void act(World world, Burrow burrow) {
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
                        Set<Location> nearby = world.getSurroundingTiles(direction);
                        List<Location> li = new ArrayList<>(nearby);

                        for (Location tile : li) {
                            if (world.getTile(tile) != null) {

                            }
                        }
                    }
                    world.move(this, direction);
                }
            }
        }

        if (world.isNight() && world.getTile(rabbitLocation) == burrow) {
            this.enterBurrow(world);
        }

        if (inBurrow && world.isDay()) {
            this.leaveBurrow(world);
        }
    }

    public void digBurrow(World world) {
        Location currentLocation = world.getLocation(this);
        if (world.containsNonBlocking(currentLocation)) {
            Burrow burrow = new Burrow(3, 3, currentLocation.getX(), currentLocation.getY());
            world.add(burrow);
            world.setTile(currentLocation, burrow);
            burrowLocation = world.getLocation(burrow);
        }
    }

    public void enterBurrow(World world) {
        world.remove(this);
        inBurrow = true;
    }

    public void leaveBurrow(World world) {
        Location currentLocation = world.getLocation(this);
        world.setTile(currentLocation, this);
    }

    public void eat(World world) {
        if (foodType.contains(FoodType.plant)) {

        }
    }

    public int getEnergy() {
        return energy;
    }

    public int getHealth() {
        return health;
    }
}
