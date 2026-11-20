public class Structure {
    private int health;
    FoodType foodType;

    public Structure(int health) {
        this.health = health;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void damageStructure(int damage) {
        health -= damage;
        if (health <= 0) {
            //Death
        }
    }
}
