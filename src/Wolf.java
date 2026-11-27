import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Wolf extends Animal implements DynamicDisplayInformationProvider {
    private List<Wolf> pack;
    private WolfDen den;

    public Wolf(int energy, int health, int age) {
        super(energy, health, age);
        this.pack = new ArrayList<>();
        this.den = null;
        foodType.add(FoodType.meat);
        largeAnimal = true;
    }

    public void setPack(List<Wolf> newPack) {
        pack = new ArrayList<>(newPack);
    }

    public void addToPack(Wolf wolf) {
        pack.add(wolf);
    }

    public void removeFromPack(Wolf wolf) {
        pack.remove(wolf);
    }

    public Wolf createOffspring() {
        Wolf baby = new Wolf(3, 5, 0);
        for (Wolf wolf : pack) {
            wolf.addToPack(baby);
        }
        baby.setPack(pack);
        return baby;
    }

    public void onAnimalDeath(World world) {
        for (Wolf wolf : pack) {
            wolf.removeFromPack(this);
        }
    }

    public Location getLocation(World world) {
        return null;
    }

    @Override
    public void act(World world) {
        super.act(world);


    }

    @Override
    public DisplayInformation getInformation() {
        if (age >= 2) {
            return new DisplayInformation(Color.red, "wolf");
        }
        return new DisplayInformation(Color.red, "wolf-small");
    }
}
