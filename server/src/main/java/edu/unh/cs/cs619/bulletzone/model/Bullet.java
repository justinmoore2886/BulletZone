package edu.unh.cs.cs619.bulletzone.model;

/**
 * <h1> Bullet Class </h1>
 * This class handles all of the bullet attributes such as damage.
 * @author Bing
 * @version 1.0
 * @since Halloween
 */
public class Bullet extends FieldEntity {

    private long tankId;
    private Direction direction;
    private int damage, bulletId;
    private int terrain=0;

    /**
     * This is the constructor. It sets damage, tankId, and direction.
     * @param tankId
     * @param direction
     * @param damage
     */
    public Bullet(long tankId, Direction direction, int damage) {
        this.damage = damage;
        this.setTankId(tankId);
        this.setDirection(direction);
    }

    /**
     * This method assigns the bullet a specific int value that will be parsed later.
     * @return int val for parsing
     */
    @Override
    public int getIntValue() {
        if(this.getDirection() == Direction.Up)
            return (int) (2000000 + 100000*terrain + 1000 * tankId + damage * 10 + 0);

        else if(this.getDirection() == Direction.Right)
            return (int) (2000000 + 100000*terrain + 1000 * tankId + damage * 10 + 2);

        else if(this.getDirection() == Direction.Down)
            return (int) (2000000 + 100000*terrain + 1000 * tankId + damage * 10 + 4);

        else
            return (int) (2000000 + 100000*terrain + 1000 * tankId + damage * 10 + 6);
    }

    @Override
    public String toString() {
        return "B";
    }

    /**
     * This method returns a new bullet with same attributes
     * @return Bullet Object
     */
    @Override
    public FieldEntity copy() {
        return new Bullet(tankId, direction, damage);
    }

    public long getTankId() {
        return tankId;
    }

    public void setTankId(long tankId) {
        this.tankId = tankId;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setBulletId(int bulletId){
        this.bulletId = bulletId;
    }

    public int getBulletId(){
        return bulletId;
    }

    public boolean hit(Bullet bullet){
        setDamage(0);
        return false;
    }

    public void setChild(FieldEntity child) {
        terrain = child.getIntValue()%10;
        this.child = child;
    }

    @Override
    public boolean action(Tank vehicle){
        vehicle.hit(this);
        vehicle.setChild(getChild());
        return true;
    }
}
