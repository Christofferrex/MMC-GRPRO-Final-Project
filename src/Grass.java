import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public class Grass extends Structure implements Actor, NonBlocking {
    private final static int minHealth = 2, maxHealth = 3;
    private final Random r;

    public Grass(int health) {
        super(health);
        r = new Random();
        foodType = FoodType.plant;
    }

    public static int getMinHealth() { return minHealth; }

    public static int getMaxHealth() { return maxHealth; }

    @Override
    public void act(World world) {
        if (r.nextDouble() > 0.025) return;

        List<Location> surroundingTiles = new ArrayList<>(world.getSurroundingTiles());
        List<Location> validLocations = new ArrayList<>();
        for (Location location : surroundingTiles) {
            if (!world.containsNonBlocking(location)) {
                validLocations.add(location);
            }
        }
        if (validLocations.isEmpty()) return;

        Location grassLocation = validLocations.get(r.nextInt(0, validLocations.size()));
        Grass grass = new Grass(r.nextInt(minHealth, maxHealth));
        world.setTile(grassLocation, grass);
    }
}