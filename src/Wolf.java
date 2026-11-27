import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Wolf extends Animal implements DynamicDisplayInformationProvider {
    private List<Wolf> pack;
    private WolfDen den;
    private boolean inDen;
    private Wolf closestPackmate;

    public Wolf(int energy, int health, int age, boolean inDen, WolfDen den) {
        super(energy, health, age);
        this.pack = new ArrayList<>();
        this.den = den;
        this.inDen = inDen;
        closestPackmate = null;
        foodType.add(FoodType.meat);
        largeAnimal = true;
    }

    public void setPack(List<Wolf> newPack) {
        pack = new ArrayList<>(newPack);
    }

    public void addToPack(Wolf wolf) {
        pack.add(wolf);
    }

    public void removeFromPack(Wolf wolf) {
        pack.remove(wolf);
    }

    public Wolf createOffspring() {
        Wolf baby = new Wolf(3, 5, 0, true, den);
        for (Wolf wolf : pack) {
            wolf.addToPack(baby);
        }
        baby.setPack(pack);
        return baby;
    }

    public void onAnimalDeath(World world) {
        List<Wolf> cachedPack = new ArrayList<>(pack);
        for (Wolf wolf : cachedPack) {
            wolf.removeFromPack(this);
        }
    }

    public Location getLocation(World world) {
        return !inDen && world.contains(this) ? world.getLocation(this) : den.denLocation();
    }

    @Override
    public void act(World world) {
        super.act(world);

        if (world.isDay()) {
            //leaves it's burrow if the rabbit is in one
            if (inDen) {
                this.leaveDen(world);
                if (inDen) return;
                if (!den.isDenizen(this)) {
                    den = null;
                }
            }
        }

        if (!world.contains(this)) return;

        //Attack foreign wolf if nearby
        if (inDen) return;
        Set<Location> neighboringTiles = world.getSurroundingTiles(getLocation(world));
        for (Location location : neighboringTiles) {
            Object potentialOpponent = world.getTile(location);
            if (potentialOpponent instanceof Animal enemy && !pack.contains(potentialOpponent)) {
                enemy.takeDamage(1);
                return;
            }
            else if (!hasEaten && potentialOpponent instanceof Carcass carcass) {
                carcass.damageStructure(1, world);
                eat();
                return;
            }
        }

        //Pathfind towards other wolf
        if (smallestDistanceToPack(world) > 2) {
            moveTowards(world, closestPackmate.getLocation(world));
            return;
        }

        //If night go to bed and fuck
        if (world.isNight()) {
            nightLogic(world);
            return;
        }

        //Random Movement
        randomMovement(world);
    }

    @Override
    public DisplayInformation getInformation() {
        if (age >= 2) {
            return new DisplayInformation(Color.red, "wolf");
        }
        return new DisplayInformation(Color.red, "wolf-small");
    }

    private void nightLogic(World world) {
        if (!inDen && den != null) { //Pathfind towards Den
            moveTowards(world, den.denLocation());
        } else if (den == null) { //Find or create Den
            searchForAndJoinDen();
            if (den == null) this.digDen(world);
        }

        if (!inDen && den != null) {
            Set<Location> surroundingTiles = world.getSurroundingTiles();
            for (Location location : surroundingTiles) {
                if (world.getNonBlocking(location) == den) {
                    this.enterDen(world);
                    break;
                }
            }
        } else if (inDen) {
            if (den.reproductionAvailable()) {
                Wolf baby = this.createOffspring();
                world.add(baby);
                den.addDenizen(baby);
                den.animalEntering(baby);
            }
        }
    }

    private double smallestDistanceToPack(World world) {
        double smallestDistance = Double.MAX_VALUE;
        Location ownLocation = getLocation(world);
        for (Wolf wolf : pack) {
            Location wolfLocation = wolf.getLocation(world);
            double distance = Math.sqrt(Math.pow((wolfLocation.getX() - ownLocation.getX()), 2) + Math.pow((wolfLocation.getY() - ownLocation.getY()), 2));
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestPackmate = wolf;
            }
        }
        return smallestDistance;
    }

    public void searchForAndJoinDen() {
        for (Wolf wolf : pack) {
            if (wolf.den != null) {
                den = wolf.den;
                den.addDenizen(this);
            }
        }
    }

    public void digDen(World world) {
        Location currentLocation = world.getLocation(this);
        Object potentialObstacle = world.getNonBlocking(currentLocation);
        if (!(potentialObstacle instanceof WolfDen)) {
            den = new WolfDen(3, 3, new Location(currentLocation.getX(), currentLocation.getY()));
            den.addDenizen(this);
            if (potentialObstacle != null) world.delete(potentialObstacle);
            world.add(den);
            world.setTile(currentLocation, den);
        }
    }

    public void enterDen(World world) {
        world.remove(this);
        inDen = true;
        den.animalEntering(this);
    }

    public void leaveDen(World world) {
        List<Location> emptySurroundings = new ArrayList<>(world.getEmptySurroundingTiles(den.denLocation()));
        if (emptySurroundings.isEmpty()) return;
        Location targetLocation = emptySurroundings.get(r.nextInt(0, emptySurroundings.size()));
        world.setTile(targetLocation, this);
        world.setCurrentLocation(targetLocation);
        inDen = false;
        den.animalLeaving(this);
    }
}