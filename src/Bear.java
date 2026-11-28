import itumulator.world.*;
import itumulator.simulator.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.util.*;
import java.util.List;
import java.awt.*;

public class Bear extends Animal implements DynamicDisplayInformationProvider{
    private int strength;
    private Location territoryCenter;
    private boolean sleeping;
    private Location preyLocation;

    Bear(int energy, int health, int age, Location territoryCenter){
        super(energy, health, age);
        foodType.add(FoodType.meat);
        foodType.add(FoodType.plant);
        strength = 2;
        largeAnimal = true;
        this.territoryCenter = territoryCenter;
        sleeping = false;
        preyLocation = null;

    }

    public Set<Location> territory(World world){
        Set<Location> area = world.getSurroundingTiles(territoryCenter, world.getSize()/4);
        area.add(territoryCenter);
        return area;
    }

    public void act(World world) {
        super.act(world);
        if(dead) return;
        if(world.isDay()) dayLogic(world);
        isSleeping(world);

    }

    public void dayLogic(World world){
        if(!world.contains(this)) return;

        boolean ate = false;
        if(isHunting(world, territory(world))){
            hunt(world);
        }else{for (int currentEnergy = energy == 0 ? 1 : energy; currentEnergy > 0; currentEnergy--) {
                if (!this.hasEaten) ate = findAndEatFood(world, world.getSurroundingTiles());
                if (!ate) randomMovement(world);
            }
        }
    }

    @Override
    protected void randomMovement(World world){
        Set<Location> neighbours = world.getEmptySurroundingTiles(getLocation(world));
        Set<Location> roaming = territory(world);
        neighbours.retainAll(roaming);
        List<Location> listOfNeighbours = new ArrayList<>(neighbours);
        if(!listOfNeighbours.isEmpty() && world.contains(this)){
            world.move(this, listOfNeighbours.get(r.nextInt(0, listOfNeighbours.size())));
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

    public boolean isHunting(World world, Set<Location> territory){
        for(Location plot : territory){
            if(world.getTile(plot) instanceof Animal prey && prey != this){
                preyLocation = prey.getLocation(world);
                return true;
            }
        }
        return false;
    }

    public void hunt(World world) {
        moveTowards(world, preyLocation);
        for (Location neighbour : world.getSurroundingTiles()) {
            if (world.getTile(neighbour) instanceof Animal prey && prey != this) {
                prey.takeDamage(strength);
            }
        }
    }

    public void isSleeping(World world){
        if(world.isNight()){
            this.sleeping = true;
        }else{
            this.sleeping = false;
        }
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
            if(sleeping){ return new DisplayInformation(Color.red, "bear-sleeping");}
            return new DisplayInformation(Color.red, "bear");
        }
        if(sleeping){ return new DisplayInformation(Color.red, "bear-small-sleeping");}
        return new DisplayInformation(Color.red, "bear-small");
    }

    public Bear createOffspring(){
        return null; //if going in rut gets added, should be easy enough
    }

    public void onAnimalDeath(World world) {

    }

    public Location getLocation(World world) {
        if(world.contains(this)){return world.getLocation(this);}
        return null;
    }
}