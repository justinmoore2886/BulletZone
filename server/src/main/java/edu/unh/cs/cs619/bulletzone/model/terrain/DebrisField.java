package edu.unh.cs.cs619.bulletzone.model.terrain;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

/**
 * <h1> Debris Field Class! </h1>
 * This class handles Debris Field Terrain.
 * @author Ryan
 * @version 1.0
 * @since Thanksgiving
 */
public class DebrisField extends FieldEntity {
    int pos;

    public DebrisField(){}
    public DebrisField(int pos){
        this.pos = pos;
    }
    @Override
    public FieldEntity copy() {
        return new DebrisField(pos);
    }

    @Override
    public int getIntValue() {
        return 4500002;
    }

    @Override
    public String toString() {
        return "D";
    }

    public int getPos(){
        return pos;
    }

    /**
     * This method applies an action on a vehicle based on debris field properties
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle){
        System.out.println("tank on debris");
        if(vehicle.getIdentifier() == 4){
            return false;
        }
        if(vehicle.getIdentifier() == 2) {
            vehicle.setTerrain(2000);
        }
        vehicle.setChild(this);
        vehicle.getPowerUp().applyEffect(vehicle);
        return true;
    }
}
