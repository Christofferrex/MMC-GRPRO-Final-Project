import itumulator.world.NonBlocking;
import itumulator.world.Location;

public class Burrow extends Structure implements NonBlocking {
    private int capacity;
    private final Rabbit[] rabbits;
    private Location location;

    public Burrow(int capacity, int health, int x, int y) {
        super(health);
        this.capacity = capacity;
        rabbits = new Rabbit[capacity];
        location = new Location(x, y);
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

    public Location burrowLocation(){
        return location;
    }
}