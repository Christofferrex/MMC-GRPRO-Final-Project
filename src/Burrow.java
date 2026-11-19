import itumulator.world.NonBlocking;
import itumulator.world.Location;

public class Burrow extends Structure implements NonBlocking {
    private int capacity;
    private final Rabbit[] rabbits;

    public Burrow(int capacity, int health){
        super(health);
        this.capacity = capacity;
        rabbits = new Rabbit[capacity];
    }

    public boolean isFull(){
        for(int i = 0; i < rabbits.length; i++) {
            if (rabbits[i] == null){
                return false;
            }
        }
        return true;
    }


}