package edu.unh.cs.cs619.bulletzone.model.terrain;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public class Empty extends FieldEntity {
    int pos;

    public Empty(){}
    public Empty(int pos){
        this.pos = pos;
    }
    @Override
    public FieldEntity copy() {
        return new Empty(pos);
    }

    @Override
    public int getIntValue() {
        return 0;
    }

    @Override
    public String toString() {
        return "E";
    }

    public int getPos(){
        return pos;
    }

    /**
     * This method applies an action on a vehicle based on emptyå properties
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle){

        System.out.println("vehicle on empty");
        if(vehicle.getIdentifier() == 4){
            return false;
        }
        if(vehicle.getIdentifier() == 2){
            vehicle.setAllowedMoveInterval(1000);
            vehicle.setAllowedFireInterval(250);
            vehicle.setAllowedNumberOfBullets(6);
        }
        else {
            vehicle.setAllowedMoveInterval(500);
            vehicle.setAllowedFireInterval(1500);
            vehicle.setAllowedNumberOfBullets(2);
        }


        vehicle.setChild(this);
        vehicle.getPowerUp().applyEffect(vehicle);
        return true;
    }
}
