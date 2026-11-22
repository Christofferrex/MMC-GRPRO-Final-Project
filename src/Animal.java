import itumulator.simulator.*;
import itumulator.world.*;
import java.util.*;

public abstract class Animal implements Actor {
    protected int energy;
    protected int health;
    protected int age;
    protected boolean hasEaten = false;
    protected List<FoodType> foodType;
    protected final Random r;

    //constructor
    Animal(int energy, int health, int age) {
        this.energy = energy;
        this.health = health;
        this.age = age;
        foodType = new ArrayList<>();
        r = new Random();
    }

    //abstract method for breeding
    public abstract Object createOffspring();

    //adds age
    public void ageUp() {
        age++;
        if (this.age % 2 == 0) {
            this.energyDown();
        }
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
        for (int currentEnergy = energy; currentEnergy > 0; currentEnergy--) {
            List<Location> list = new ArrayList<>(world.getEmptySurroundingTiles());
            world.move(this, list.get(r.nextInt(0, list.size())));
        }
    }
}
