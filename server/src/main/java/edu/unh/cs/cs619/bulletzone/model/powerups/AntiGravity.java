package edu.unh.cs.cs619.bulletzone.model.powerups;


import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

/**
 * <h1> Anti Gravity Class! </h1>
 * This class handles Anti Gravity power-ups.
 * @author Ryan
 * @version 1.0
 * @since Thanksgiving
 */
public class AntiGravity extends PowerUp {

    public AntiGravity(int pos){
        this.pos = pos;
    }
    @Override
    public FieldEntity copy() {
        return new AntiGravity(pos);
    }

    @Override
    public int getIntValue() {
        return 4600001;
    }

    @Override
    public String toString() {
        return "A";
    }

    /**
     * This method applies the power ups effect to the user
     * @param vehicle The users vehicle
     */
    public void applyEffect(Tank vehicle){

        if(vehicle.getIdentifier() == 2){ // soldier
            vehicle.setAllowedMoveInterval(500);
            vehicle.setAllowedFireInterval(350);
        }
        //add for ship
        else if(vehicle.getIdentifier() == 1) {
            vehicle.setAllowedMoveInterval(250);
            vehicle.setAllowedFireInterval(600);
        }
        else if(vehicle.getIdentifier() == 4){
            vehicle.setAllowedMoveInterval(375);
            vehicle.setAllowedFireInterval(500);
            vehicle.setAllowedShipSideFireInterval(350);
        }
    }
}
