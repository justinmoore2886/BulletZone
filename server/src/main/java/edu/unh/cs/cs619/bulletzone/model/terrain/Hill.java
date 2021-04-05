package edu.unh.cs.cs619.bulletzone.model.terrain;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

/**
 * <h1> Hill Class! </h1>
 * This class handles Hill terrain.
 * @author Ryan
 * @version 1.0
 * @since Thanksgiving
 */
public class Hill extends FieldEntity {
    int pos;

    public Hill(){}
    public Hill(int pos){
        this.pos = pos;
    }
    @Override
    public FieldEntity copy() {
        return new Hill(pos);
    }

    @Override
    public int getIntValue() {
        return 4500003;
    }

    @Override
    public String toString() {
        return "H";
    }

    public int getPos(){
        return pos;
    }

    /**
     * This method applies an action on a vehicle based on hill properties
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle){
        System.out.println("tank on hill");
        if(vehicle.getIdentifier() == 4){
            return false;
        }
        if(vehicle.getIdentifier() == 1) {
            vehicle.setTerrain(1000);
        }
        vehicle.setChild(this);
        vehicle.getPowerUp().applyEffect(vehicle);
        return true;
    }
}
