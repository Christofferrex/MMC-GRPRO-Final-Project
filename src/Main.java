import java.awt.Color;
import java.io.File;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        InputHandler ir = new InputHandler(new File("resources/data/Tema1/t1-2cde.txt"));

        Program p = new Program(ir.getWorldSize(), 800, 75);
        World w = p.getWorld();

        p.setDisplayInformation(Grass.class, new DisplayInformation(Color.green, "grass"));
        p.setDisplayInformation(Burrow.class, new DisplayInformation(Color.green, "hole-small"));
        p.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.green, "rabbit-large"));
        p.setDisplayInformation(BerryBush.class, new DisplayInformation(Color.green, "bush"));

        ir.spawnObjects(w);

        // w.setTile(new Location(0, 0), new <MyClass>());
        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

//        Grass Spawning:
//        w.setTile(new Location(0,0), new Grass(r.nextInt(1, 3)));
//        p.setDisplayInformation(Grass.class, new DisplayInformation(Color.green, "grass"));

        p.show();
    }
}