import itumulator.world.NonBlocking;
import itumulator.world.Location;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Burrow extends Structure implements NonBlocking {
    private int capacity;
    private final Rabbit[] rabbits;
    private List<Rabbit> denizens;
    private Location location;

    public Burrow(int capacity, int health, int x, int y) {
        super(health);
        this.capacity = capacity;
        rabbits = new Rabbit[capacity];
        denizens = new ArrayList<>();
        location = new Location(x, y);
    }

    public void addDenizen(Rabbit rabbit) {
        if (atFullCapacity()) return;
        denizens.add(rabbit);
    }

    public boolean isDenizen(Rabbit rabbit) {
        return denizens.contains(rabbit);
    }

    public void removeDenizen(Rabbit rabbit) {
        denizens.remove(rabbit);
    }

    public void rabbitEntering(Rabbit rabbit){
        if(isFull()) return;
        for(int i = 0; i < rabbits.length; i++){
            if(rabbits[i] == null){
                rabbits[i] = rabbit;
                break;
            }
        }
    }

    public void rabbitLeaving(Rabbit rabbit){
        for(int i = 0; i < rabbits.length; i++){
            if(rabbits[i] == rabbit){
                rabbits[i] = null;
                break;
            }
        }
    }

    public boolean isFull(){
        for(int i = 0; i < rabbits.length; i++) {
            if (rabbits[i] == null){
                return false;
            }
        }
        return true;
    }

    public boolean atFullCapacity() {
        return denizens.size() == capacity;
    }

    public boolean reproductionAvailable() {
        if (rabbitAmount() < 2) return false;
        int rabbitsReady = 0;
        List<Rabbit> readyRabbits = new ArrayList<>();
        for (Rabbit rabbit : rabbits) {
            if (rabbit != null && !rabbit.getReproducedTonight() && rabbit.age >= 2) {
                rabbitsReady++;
                readyRabbits.add(rabbit);
            }
        }
        if (rabbitsReady >= 2) {
            readyRabbits.get(0).setReproducedTonight(true);
            readyRabbits.get(1).setReproducedTonight(true);
            return true;
        }
        return false;
    }

    public Location burrowLocation(){
        return location;
    }

    public int rabbitAmount() {
        int amount = 0;
        for(int i = 0; i < rabbits.length; i++){
            if(rabbits[i] != null){
                amount++;
            }
        }
        return amount;
    }
}