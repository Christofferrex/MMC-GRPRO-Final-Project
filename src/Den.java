import itumulator.world.Location;
import itumulator.world.NonBlocking;

import java.util.ArrayList;
import java.util.List;

public abstract class Den extends Structure implements NonBlocking {
    protected int capacity;
    protected final Animal[] currentAnimals;
    protected List<Animal> denizens;
    protected Location location;

    public Den(int health, int capacity, Location location) {
        super(health);
        this.capacity = capacity;
        currentAnimals = new Animal[capacity];
        denizens = new ArrayList<>();
        this.location = location;
    }

    public void addDenizen(Animal animal) {
        if (atFullCapacity()) return;
        denizens.add(animal);
    }

    public boolean isDenizen(Animal animal) {
        return denizens.contains(animal);
    }

    public void removeDenizen(Animal animal) {
        denizens.remove(animal);
    }

    public void animalEntering(Animal animal){
        if(isFull()) return;
        for(int i = 0; i < currentAnimals.length; i++){
            if(currentAnimals[i] == null){
                currentAnimals[i] = animal;
                break;
            }
        }
    }

    public void animalLeaving(Animal animal){
        for(int i = 0; i < currentAnimals.length; i++){
            if(currentAnimals[i] == animal){
                currentAnimals[i] = null;
                break;
            }
        }
    }

    public boolean isFull(){
        for(int i = 0; i < currentAnimals.length; i++) {
            if (currentAnimals[i] == null){
                return false;
            }
        }
        return true;
    }

    public boolean atFullCapacity() {
        return denizens.size() == capacity;
    }

    public boolean reproductionAvailable() {
        if (animalAmount() < 2) return false;
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

    public Location denLocation(){
        return location;
    }

    public int animalAmount() {
        int amount = 0;
        for(int i = 0; i < currentAnimals.length; i++){
            if(currentAnimals[i] != null){
                amount++;
            }
        }
        return amount;
    }
}