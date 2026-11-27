import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class BerryBush extends Structure implements Actor, DynamicDisplayInformationProvider {
    private boolean berriesAvailable;
    private final Random r;

    public BerryBush(int health) {
        super(health);
        foodType = FoodType.plant;
        berriesAvailable = false;
        r = new Random();
    }

    public int getHealth() {
        return health;
    }

    @Override
    public DisplayInformation getInformation() {
        if (berriesAvailable) {
            return new DisplayInformation(Color.blue, "bush-berries");
        }
        return new DisplayInformation(Color.green, "bush");
    }

    @Override
    public void act(World world) {
        if (r.nextDouble() > 0.01) return;
        berriesAvailable = true;
    }

    public void setBerriesAvailable() {
        berriesAvailable = false;
    }

    public boolean isBerriesAvailable(){
        return berriesAvailable;
    }
}
