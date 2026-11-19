import java.util.*;
import itumulator.simulator.*;
import itumulator.world.*;

public class Rabbit extends Animal {

    Rabbit(int energy, int health, int age) {
        super(energy, health, age);
        foodType.add(FoodType.plant);
    }

    public void digBurrow() {

    }

    public void enterBurrow() {

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
