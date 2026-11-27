import itumulator.world.Location;
import itumulator.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class InputHandler {
    private final int worldSize;
    private final Scanner scanner;
    Random r;

    public InputHandler(File file) {
        r = new Random();

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.worldSize = scanner.nextInt();
        scanner.nextLine();
    }

    public void spawnObjects(World w) {
        while(scanner.hasNextLine()) {
            String[] keyValuePair = scanner.nextLine().split(" ");
            String[] spawnInterval = keyValuePair[1].split("-");
            Location territoryCenter = null;
            int loopAmount;

            if (keyValuePair.length > 2) {
                territoryCenter = new Location(Integer.parseInt(keyValuePair[2].substring(1,2)), Integer.parseInt(keyValuePair[2].substring(3,4)));
            }

            if (spawnInterval.length > 1) {
                loopAmount = r.nextInt(Integer.parseInt(spawnInterval[0]), Integer.parseInt(spawnInterval[1]) + 1);
            }
            else {
                loopAmount = Integer.parseInt(keyValuePair[1]);
            }

            List<Wolf> cachedWolfPack = new ArrayList<>();
            for (int i = 0; i < loopAmount; i++) {
                Object spawnedObject;

                Location location = getRandomEmptyLocation(w);
                if (location == null) throw new RuntimeException("Spawn Location null");

                spawnedObject = switch (keyValuePair[0].toLowerCase()) {
                    case "grass" -> new Grass(r.nextInt(Grass.getMinHealth(), Grass.getMaxHealth()));
                    case "rabbit" -> new Rabbit(5, 3, 2, false, null);
                    case "burrow" -> new Burrow(3, 5, location);
                    case "berrybush" -> new BerryBush(1);
                    case "carcass" -> new Carcass(3,false);
                    case "largecarcass" -> new Carcass(6, true);
                    case "bear" -> new Bear(3, 10, 3, territoryCenter == null ? location : territoryCenter);
                    case "wolf" -> new Wolf(3, 5, 2, false, null);
                    default -> null;
                };

                if (spawnedObject instanceof Wolf) cachedWolfPack.add((Wolf)spawnedObject);

                w.setTile(location, spawnedObject);
            }

            for (Wolf wolf : cachedWolfPack) {
                wolf.setPack(cachedWolfPack);
            }
        }
    }

    public Location getRandomEmptyLocation(World w) {
        Location location = new Location(-1, -1);
        int attempts = 0;
        while (location.getX() == -1) {
            Location randomLocation = new Location(r.nextInt(0, worldSize), r.nextInt(0, worldSize));
            if (!w.containsNonBlocking(randomLocation) && w.isTileEmpty(randomLocation)) {
                location = randomLocation;
            }

            attempts++;
            if (attempts > 200) return null;
        }
        return location;
    }

    public int getWorldSize() {
        return worldSize;
    }
}
