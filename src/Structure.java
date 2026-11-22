import itumulator.world.World;

public abstract class Structure {
    protected int health;
    protected FoodType foodType;

    public Structure(int health) {
        this.health = health;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void damageStructure(int damage, World w) {
        health -= damage;
        if (health <= 0) {
            w.delete(this);
        }
    }
}
