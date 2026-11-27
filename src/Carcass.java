import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class Carcass extends Structure implements Actor, DynamicDisplayInformationProvider {
    private final boolean isLarge;
    private int timeInWorld = 0;
    private int allowedTimeInWorld = 20;

    Carcass(int health, boolean isLarge) {
        super(health);
        this.isLarge = isLarge;
        foodType = FoodType.meat;
        if (isLarge) {
            allowedTimeInWorld = 40;
        }
    }

    public boolean getSize() {
        return isLarge;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public DisplayInformation getInformation() {
        if (isLarge) {
            return new DisplayInformation(Color.red, "carcass");
        }
        return new DisplayInformation(Color.red, "carcass-small");
    }

    @Override
    public void act(World world) {
        timeInWorld++;
        if (timeInWorld >= allowedTimeInWorld) {
                this.decompose(world);
        }
    }

    public void decompose(World world) {
        world.delete(this);
        if (world.getNonBlocking(world.getCurrentLocation()) != null) return;
        Grass grass = new Grass(new Random().nextInt(Grass.getMinHealth(), Grass.getMaxHealth()));
        world.add(grass);
        world.setTile(world.getCurrentLocation(), grass);
    }
}
