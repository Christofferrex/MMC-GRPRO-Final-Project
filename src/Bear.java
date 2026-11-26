import itumulator.world.*;

public class Bear extends Animal{

    Bear(int energy, int health, int age){
        super(energy, health, age);
        foodType.add(FoodType.meat);

    }

    public void act(World world) {


        if(health <= 0 || energy <= 0){
            world.delete(this);
        }
    }

    public void attack(Animal prey){
        prey.takeDamage(3);
    }
    @Override
    public Bear createOffspring(){
        return new Bear(7, 10, 0);
    }
}
