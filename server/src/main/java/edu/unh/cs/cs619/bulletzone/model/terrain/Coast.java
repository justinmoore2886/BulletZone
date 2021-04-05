package edu.unh.cs.cs619.bulletzone.model.terrain;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerRack;

public class Coast extends FieldEntity {
    int pos;

    public Coast(){}
    public Coast(int pos){
        this.pos = pos;
    }
    @Override
    public FieldEntity copy() {
        return new Coast(pos);
    }

    @Override
    public int getIntValue() {
        return 4500001;
    }

    @Override
    public String toString() {
        return "C";
    }

    public int getPos(){
        return pos;
    }

    /**
     * This method applies an action on a vehicle based on coast properties
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle) {
        if(vehicle.getIdentifier() == 2) {
            vehicle.setTerrain(1500);
            // Soldier with a power rack goes slower through coast
            if(vehicle.getPowerUp() instanceof PowerRack) {
                vehicle.setChild(this);
                vehicle.setTerrain(700);
                return true;
            }
        }
        if(vehicle.getIdentifier() == 1) {
            vehicle.setTerrain(1000);
            vehicle.setAllowedMoveInterval(1000);
        }
        vehicle.setChild(this);
        vehicle.getPowerUp().applyEffect(vehicle);
        return true;
    }

}
