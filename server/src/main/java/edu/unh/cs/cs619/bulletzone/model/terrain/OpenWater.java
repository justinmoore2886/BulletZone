package edu.unh.cs.cs619.bulletzone.model.terrain;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerRack;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerUp;

public class OpenWater extends FieldEntity {
    int pos;

    public OpenWater(){}
    public OpenWater(int pos){
        this.pos = pos;
    }
    @Override
    public FieldEntity copy() {
        return new OpenWater(pos);
    }

    @Override
    public int getIntValue() {
        return 4500004;
    }

    @Override
    public String toString() {
        return "OW";
    }

    public int getPos(){
        return pos;
    }

    /**
     * This method applies an action on a vehicle based on water properties
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle){
        if(vehicle.getIdentifier() == 2 && vehicle.getPowerUp() instanceof PowerRack){
            vehicle.powerUps = new PowerUp();
            vehicle.rack.rackIndicator = false;
            vehicle.SpawnRack(vehicle);
            vehicle.setSoldierRacked(0);
            vehicle.setChild(this);
            return true;
        }
        else if(vehicle.getIdentifier() == 2){
            vehicle.setTerrain(2000);
            vehicle.setChild(this);
            vehicle.getPowerUp().applyEffect(vehicle);
            return true;
        }
        else if(vehicle.getIdentifier() == 4) {
            vehicle.setChild(this);
            vehicle.getPowerUp().applyEffect(vehicle);
            return true;
        }
        else {
            return false;
        }
    }
}
