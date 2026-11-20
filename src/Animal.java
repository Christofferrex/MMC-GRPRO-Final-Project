import itumulator.simulator.*;
import itumulator.world.*;
import java.util.*;

public class Animal implements Actor {
    int energy;
    int health;
    int age;
    boolean hasEaten = false;
    List<FoodType> foodType;

    //constructor
    Animal(int energy, int health, int age) {
        this.energy = energy;
        this.health = health;
        this.age = age;
        foodType = new ArrayList<>();
    }

    public void breed() {

    }

    //adds age
    public void ageUp() {
        age++;
    }

    //removes energy
    public void energyDown() {
        energy--;
    }

    //sets hasEaten to true and increases health
    public void eat() {
        hasEaten = true;
        health++;
    }

    //removes health
    public void starve() {
        health--;
    }

    //sets hasEaten to false
    public void setHasEaten() {
        hasEaten = false;
    }

    //random movement
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
