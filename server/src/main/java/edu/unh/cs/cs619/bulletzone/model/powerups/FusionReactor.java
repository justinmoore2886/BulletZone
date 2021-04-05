package edu.unh.cs.cs619.bulletzone.model.powerups;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

/**
 * <h1> Fusion Reactor Class! </h1>
 * This class handles Fusion Reactor power-ups
 * @author Ryan
 * @version 1.0
 * @since Thanksgiving
 */

public class FusionReactor extends PowerUp {

    public FusionReactor(int pos){
        setPos(pos);
    }

    @Override
    public FieldEntity copy() {
        return new FusionReactor(getPos());
    }

    @Override
    public int getIntValue() {
        return 4600002;
    }

    @Override
    public String toString() {
        return "F";
    }

    /**
     * This method applies the power ups effect to the user
     * @param vehicle The users vehicle
     */
    public void applyEffect(Tank vehicle){
        if(vehicle.getIdentifier() == 2){ // soldier
            vehicle.setAllowedFireInterval(125);
            vehicle.setAllowedNumberOfBullets(12);
            vehicle.setAllowedMoveInterval(1250);
        }

        //add for ship
        else if(vehicle.getIdentifier() == 1) {
            vehicle.setAllowedFireInterval(250);
            vehicle.setAllowedNumberOfBullets(4);
            vehicle.setAllowedMoveInterval(625);
        }
        else if(vehicle.getIdentifier() == 4){
            vehicle.setAllowedFireInterval(150);
            vehicle.setAllowedShipSideFireInterval(125);
            vehicle.setAllowedNumberOfBullets(16);
            vehicle.setAllowedMoveInterval(938);
        }

    }

    /**
     * This method applies if the power up has been hit by a bullet
     * @param bullet The bullet hitting it
     */
    @Override
    public boolean hit(Bullet bullet) {
        return false;
    }

}
