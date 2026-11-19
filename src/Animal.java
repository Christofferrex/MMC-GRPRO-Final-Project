import itumulator.simulator.*;
import itumulator.world.*;
import java.util.*;

public class Animal implements Actor {
    int energy;
    int health;
    int age;
    List<FoodType> foodType;

    Animal(int energy, int health, int age) {
        this.energy = energy;
        this.health = health;
        this.age = age;
        foodType = new ArrayList<>();
    }

    public void breed() {

    }

    public void ageUp(World world) {
        if (world.isNight()) {
            age++;
        }
    }

    public void energyDown() {
        if (age % 2 == 0) {
            energy--;
        }
    }

    public void eat(World world) {
        Set<Location> neighbours = world.getSurroundingTiles();
        for (Location neighbour : neighbours) {
            if (world.containsNonBlocking(neighbour)) {

            }
        }
    }

    @Override
    public void act(World world) {
        Set<Location> neighbours = world.getEmptySurroundingTiles();
        List<Location> list;
        Random r;
        Location l;
        for (int currentEnergy = energy; currentEnergy > 0; currentEnergy--)
            neighbours = world.getEmptySurroundingTiles();
            list = new ArrayList<>(neighbours);
            r = new Random();
            l = list.get(r.nextInt(0,list.size()));
            world.move(this, l);
    }
}
