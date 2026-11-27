import itumulator.world.*;
import itumulator.simulator.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.util.*;
import java.awt.*;

public class Bear extends Animal implements DynamicDisplayInformationProvider{
    private int strength;

    Bear(int energy, int health, int age){
        super(energy, health, age);
        foodType.add(FoodType.meat);
        foodType.add(FoodType.plant);
        strength = 2;
        largeAnimal = true;
    }

    public void act(World world) {
        super.act(world);
        if(dead) return;
        if(world.isDay()) dayLogic(world);
    }

    public void dayLogic(World world){
        if(!world.contains(this)) return;

        boolean ate = false;
        for(int currentEnergy = energy == 0 ? 1 : energy; currentEnergy > 0; currentEnergy--){
            if(!this.hasEaten) ate = findAndEatFood(world, world.getSurroundingTiles());
            if(!ate) randomMovement(world);
        }
    }

    public boolean findAndEatFood(World world, Set<Location> neighbours){
        for(Location neighbour : neighbours){
            if(world.getTile(neighbour) instanceof Carcass carcass){
                this.eat();
                carcass.damageStructure(1, world);
                return true;
            }else if(world.getTile(neighbour) instanceof BerryBush berryBush && berryBush.isBerriesAvailable()){
                this.eat();
                berryBush.setBerriesAvailable();
                return true;
            }
        }
        return false;
    }

    public void attack(Animal prey){
        prey.takeDamage(strength);
    }

    @Override
    public void ageUp() {
        age++;
        if(this.age % 3 == 0){
            this.energyDown();
            strength++;
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if(age >= 3) {
            return new DisplayInformation(Color.red, "bear");
        }
        return new DisplayInformation(Color.red, "bear-small");
    }

    public Bear createOffspring(){
        return new Bear(7, 10, 0);
    }

    public void onAnimalDeath(World world) {

    }

    public Location getLocation(World world) {
        if(world.contains(this)){return world.getLocation(this);}
        return null;
    }
}