package edu.unh.cs.cs619.bulletzone.model;

/**
 * <h1> Transformer Class! </h1>
 * The Transformer class provides the necessary adjustments for a transfer during ejection or return
 * to tank.
 *
 * @author Bing
 * @version 1.0
 * @since 11/23/2018
 */
public class Transformer {
    /**
     * This method handles the ejection of soldier and its values.
     * @param tank
     */
    public void TanktoSoldier(Tank tank){
        tank.setLife(SoldierInfo.life);
        tank.setIdentifier(2);
        tank.setAllowedNumberOfBullets(SoldierInfo.allowedNumberOfBullets);
        tank.setAllowedMoveInterval(SoldierInfo.allowedMoveInterval);
        tank.setAllowedFireInterval(SoldierInfo.allowedFireInterval);
        tank.setControllerId(-1);
        //make sure all controlled attributes are set to default            ///TODO
    }

    /**
     * This method handles the return of the soldier to the tank and its values.
     * @param soldier
     * @param emptyTank
     */
    public void SoldiertoTank(Tank soldier, Tank emptyTank){
        int newidentifier;
        if(emptyTank.getIdentifier() == 3){
            newidentifier = 1;
        } else{
            newidentifier = 4;
        }
        //soldier.clearBullets();
        soldier.setIdentifier(newidentifier);
        soldier.setLife(emptyTank.getLife());
        soldier.setAllowedFireInterval(TankInfo.allowedFireInterval);
        soldier.setAllowedMoveInterval(TankInfo.allowedMoveInterval);
        soldier.setAllowedNumberOfBullets(TankInfo.allowedNumberOfBullets);
        soldier.setNumberOfBullets(0);
        soldier.setDirection(emptyTank.getDirection());
    }
}
