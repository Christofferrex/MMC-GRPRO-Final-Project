import itumulator.world.Location;

import java.util.ArrayList;
import java.util.List;

public class WolfDen extends Den {
    public WolfDen(int health, int capacity, Location location) {
        super(health, capacity, location);
    }

    @Override
    public boolean reproductionAvailable() {
        if (animalAmount() < 2 || atFullCapacity()) return false;
        int animalsReady = 0;
        List<Animal> readyAnimals = new ArrayList<>();
        for (Animal animal : currentAnimals) {
            if (animal != null && !animal.getReproducedTonight() && animal.age >= 2) {
                animalsReady++;
                readyAnimals.add(animal);
            }
        }
        if (animalsReady >= 2) {
            readyAnimals.get(0).setReproducedTonight(true);
            readyAnimals.get(1).setReproducedTonight(true);
            return true;
        }
        return false;
    }
}
