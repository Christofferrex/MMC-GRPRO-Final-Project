public class Structure {
    private int health;


    public Structure(int health) {
        this.health = health;
    }

    public void damageStructure(int damage) {
        health -= damage;
        if (health <= 0) {
            //Death
        }
    }
}
