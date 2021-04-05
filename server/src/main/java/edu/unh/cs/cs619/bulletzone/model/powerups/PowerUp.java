package edu.unh.cs.cs619.bulletzone.model.powerups;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.terrain.Coast;

public class PowerUp extends FieldEntity {
    int pos;

    @Override
    public int getIntValue() { return 0;}

    @Override
    public void setPos(int p){
        pos = p;
    }

    @Override
    public FieldEntity copy() {
        return new PowerUp();
    }

    @Override
    public String toString() {
        return "Empty PowerUp";
    }

    public int getPos(){
        return pos;
    }

    /**
     * This method does an action onto the vehicle
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle){
        System.out.println("tank on " + this.toString() + " power up");
        if(vehicle.getIdentifier() == 4 && !(this.getChild() instanceof Coast)){
            return false;
        }
        vehicle.setPowerUp(this);
        vehicle.setChild(this.getChild());
        return true;
    }

    /**
     * This method applies if the power up has been hit by a bullet
     * @param bullet The bullet hitting it
     */
    @Override
    public boolean hit(Bullet bullet) {
        getParent().clearField();
        getParent().setFieldEntity(this.getChild());
        return false;
    }

    public void eject(){}

    public void applyEffect(Tank vehicle){

    }
}
