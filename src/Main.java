import java.awt.Color;
import java.util.Random;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        int size = 5;
        Program p = new Program(size, 800, 75);
        Random r = new Random();

        World w = p.getWorld();

        // w.setTile(new Location(0, 0), new <MyClass>());

        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

//        Grass Spawning:
//        w.setTile(new Location(0,0), new Grass(r.nextInt(1, 3)));
//        p.setDisplayInformation(Grass.class, new DisplayInformation(Color.green, "grass"));

        p.show();
    }
}