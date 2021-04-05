package edu.unh.cs.cs619.bulletzone.model;

/**
 * <h1> Wall Class! </h1>
 * The class handles the creation of walls and their destruction values.
 * @author TBD
 * @version 1.0
 * @since Halloween
 */
public class Wall extends FieldEntity {
    int destructValue, pos, Life;

    /**
     * This is the constructor. It sets the destruct value.
     */
    public Wall(){
        this.destructValue = 1000;
    }

    /**
     * This is a constructor. It sets the destruct value and position value.
     * @param destructValue this is the destruct value
     * @param pos this is Position of wall
     */
    public Wall(int destructValue, int pos){
        this.destructValue = destructValue;
        this.pos = pos;
        this.Life = 3;
    }

    /**
     * This is creates a new wall with the same attributes
     * @return Wall object
     */
    @Override
    public FieldEntity copy() {
        return new Wall();
    }

    @Override
    public int getIntValue() {
        return destructValue;
    }

    @Override
    public String toString() {
        return "W";
    }

    public int getPos(){
        return pos;
    }

    public int getLife() {
        return Life;
    }

    /**
     * This method applies a hit on wall by bullet
     * @param bullet The bullet hitting it
     */
    @Override
    public boolean hit(Bullet bullet){
        if(destructValue != 1000) {
            Life -= 1;
            destructValue = 1500 + Life;
            if (getLife() == 0) {
                getParent().setFieldEntity(getChild());
            }
        }
        return false;
    }

    /**
     * This method applies an action on a vehicle based on coast properties
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle){
        return false;
    }
}
