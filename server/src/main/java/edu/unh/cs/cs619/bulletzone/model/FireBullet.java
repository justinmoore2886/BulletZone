package edu.unh.cs.cs619.bulletzone.model;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.powerups.AntiGravity;
import edu.unh.cs.cs619.bulletzone.model.powerups.FusionReactor;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerRack;
import edu.unh.cs.cs619.bulletzone.model.terrain.DebrisField;
import edu.unh.cs.cs619.bulletzone.model.terrain.Empty;
import edu.unh.cs.cs619.bulletzone.model.terrain.Hill;

/**
 * <h1> FireBullet Class! </h1>
 * This class handles firing bullets and creating them.
 */
public class FireBullet {

    /**
     * check for the number of bullet allowed for each tank on the map
     * @param tank the tank being checked
     * @return if there is less then allow of bullet on field per tank
     */
    private boolean bulletAllotment(Tank tank) {
        return tank.getNumberOfBullets() < tank.getAllowedNumberOfBullets();
    }

    /**
     * Check if the tank is alive or dead
     * @param t the tank being checked
     * @param game the game the tank is associated with
     */
    static void tankAliveCheck(Tank t, Game game) {
        if (t.getLife() <= 0 ){
            t.getParent().clearField();
            t.setParent(null);
            game.removeTank(t.getId());
        }
    }

    /**
     * Check if tanks exists around the fusion reactor and do damage to them.
     * If sign = 0, then check negative positions around tank
     * if sign = 1, then check positive positions around tank
     * @param position position in the grid
     * @param i used to find position
     * @param game current game being played
     * @param sign current game being played
     */
    static void checkTankExistence(int position, Game game, int i, int sign) {
        if(sign == 1) {
            if(position - i < game.getHolderGrid().size() - 1 && position - i > 0) {
                if (game.getHolderGrid().get(position - i).getEntity() instanceof Tank) {
                    Tank t = (Tank) game.getHolderGrid().get(position - i).getEntity();
                    t.setLife(t.getLife() - 20);
                    System.out.println("tank is hit, tank life: " + t.getLife());
                    tankAliveCheck(t, game);
                }
            }
        }
        else if(sign == 0) {
            if(position + i < game.getHolderGrid().size() - 1 && position + i > 0) {
                if (game.getHolderGrid().get(position + i).getEntity() instanceof Tank) {
                    Tank t = (Tank) game.getHolderGrid().get(position + i).getEntity();
                    t.setLife(t.getLife() - 20);
                    System.out.println("tank is hit, tank life: " + t.getLife());
                    tankAliveCheck(t, game);
                }
            }
        }
    }

    /**
     * Check if tanks exists around the fusion reactor and then do damage to them
     * @param position position in the grid
     * @param game current game being played
     */
    static boolean inflictFusionDamage(int position, Game game, long fieldEntityValue) {
        if(fieldEntityValue == 4600002) {
            checkTankExistence(position, game, 1, 1);
            checkTankExistence(position, game, 1, 0);
            checkTankExistence(position, game, 15, 1);
            checkTankExistence(position, game, 15, 0);
            checkTankExistence(position, game, 17, 1);
            checkTankExistence(position, game, 17, 0);
            checkTankExistence(position, game, 16, 1);
            checkTankExistence(position, game, 16, 0);
            return true;
        }
        return false;
    }

    private int bulletDamageCheck(Tank tank, Direction direction, int[] bulletDamage){
        int bulletDamagefinal;
        if(tank.getIdentifier() == 2){
            bulletDamagefinal = 5;
        } else if (tank.getIdentifier() == 1 || tank.getIdentifier() == 3){
            bulletDamagefinal = bulletDamage[0];
        } else {
            if(tank.getDirection() == direction){
                bulletDamagefinal = 40;
            } else{
                bulletDamagefinal = 5;
            }
        }
        return bulletDamagefinal;
    }

    /**
     * This method is the main method that creates and fires a bullet.
     * @param tank
     * @param direction
     * @param parent
     * @param directionFire
     * @param trackActiveBullets
     * @param bulletDamage
     * @param BULLET_PERIOD
     * @param game
     * @param timer
     * @param monitor
     * @return bool success or not
     */
    public boolean createBullet(Tank tank, Direction direction, FieldHolder parent, Direction directionFire, int[] trackActiveBullets, int[] bulletDamage, int BULLET_PERIOD, Game game, Timer timer, Object monitor,boolean debug) {
        if (!bulletAllotment(tank)) //check for allotment
            return false;
        //System.out.println("NumOfBullets: " + tank.getNumberOfBullets());
        //System.out.println("AllowedNumOfBullets: " + tank.getAllowedNumberOfBullets());

        //unnecessary? Already logic for removal of soldiers or tanks bullets when ejecting entering
        /*if(tank.getTransformerCalled() == true){
            tank.clearBullets();
            tank.setTransformerCalled(false);
        }*/
/*
        if (!(bulletType >= 1 && bulletType <= 3)) {
            System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
            bulletType = 1;
        }
*/
        //0 is inactive bullet and 1 is active bullet
        //know how many bullets are on the board
        //ensures no two bullets have the same id
        int bulletId = 0;
        for (int i = 0; i < 16; i++) {
            if (trackActiveBullets[i] == 0) {
                bulletId = i;
                trackActiveBullets[i] = 1;
                break;
            }
        }
        int bulletDamageFinal = bulletDamageCheck(tank, directionFire, bulletDamage);

        // wrapping this because I don't really know why this is here?!
        if(!debug){
            // Create a new bullet to fire
            if(tank.getIdentifier() == 2 && tank.child.toString() == "OW"){ // if soldier, fail to fire
                return false;
            }
        }
        if(tank.getIdentifier()>=4 || tank.getIdentifier() == 2) {
            direction = directionFire;
        }
        final Bullet bullet = new Bullet(tank.getId(), direction, bulletDamageFinal);
        tank.addBullet(bullet, bulletId); //update tank's bullets
        tank.setNumberOfBullets(tank.getNumberOfBullets() + 1); //update the allotment
        // Set the same parent for the bullet.
        // This should be only a one way reference.
        bullet.setParent(parent);
        bullet.setBulletId(bulletId);
        bullet.setChild(parent.getEntity());

        return startBullet(timer, BULLET_PERIOD, trackActiveBullets, monitor, tank, bullet, game);
    }

    //handles the bullet's activity and its lifespan
    protected boolean startBullet(Timer timer, int BULLET_PERIOD, int[] trackActiveBullets, Object monitor, Tank tank, Bullet bullet, Game game){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (monitor) {
                    System.out.println("Active Bullet: " + tank.getNumberOfBullets() + "---- Bullet ID: " + bullet.getIntValue() + "bullet damage" + bullet.getDamage());
                    FieldHolder currentField = bullet.getParent();
                    Direction direction = bullet.getDirection();
                    FieldHolder nextField = currentField.getNeighbor(direction);

                    //removes bullet if bullet was set to a 'clear' state of 0 damage
                    //cases: soldier entered tank, soldier ejected, bullets collided
                    if(bullet.getDamage() == 0){
                        currentField.setFieldEntity(bullet.getChild());
                        trackActiveBullets[bullet.getBulletId()] = 0;
                        tank.setNumberOfBullets(tank.getNumberOfBullets() - 1);
                        tank.addBullet(null, bullet.getBulletId());
                        cancel();
                    }
                    //if bullet hit object
                    else if(!nextField.getEntity().hit(bullet)){
                        //do fusion AOE damage if object was fusion core
                        if(inflictFusionDamage(nextField.getEntity().getPos(), game, nextField.getEntity().getIntValue())){
                            nextField.setFieldEntity(nextField.getEntity().getChild());
                        }
                        //if vehicle hit is a tank or ship and it doesn't die, eject a power-up
                        else if(nextField.getEntity() instanceof Tank && ((Tank)(nextField.getEntity())).getLife() > 0){
                            if(((Tank) nextField.getEntity()).getIdentifier() != 2){
                                if(((Tank) nextField.getEntity()).getPowerUp() instanceof PowerRack)
                                    ((Tank) nextField.getEntity()).SpawnRack((Tank)nextField.getEntity());
                                ((Tank) nextField.getEntity()).ejectPowerUp();
                            }
                        }
                        //if killed vehicle, remove vehicle from the game memory and visually
                        else if(nextField.getEntity() instanceof Tank && ((Tank)(nextField.getEntity())).getLife() <=0 ) {
                            //remove vehicle visually and from game memory
                            //if(((Tank) nextField.getEntity()).getIdentifier() != 3) {}
                            game.removeTank(((Tank) (nextField.getEntity())).getId());

                            nextField.setFieldEntity(nextField.getEntity().getChild());

                            if(!game.hasEmptyTankWithIdentifier(3))
                                game.addEmptyVehicle(3);
                            if(!game.hasEmptyTankWithIdentifier(5))
                                game.addEmptyVehicle(5);
                        }
                        //remove bullet visually and cancel run
                        currentField.setFieldEntity(bullet.getChild());
                        trackActiveBullets[bullet.getBulletId()] = 0;
                        tank.setNumberOfBullets(tank.getNumberOfBullets() - 1);
                        tank.addBullet(null, bullet.getBulletId());
                        cancel();
                    }
                    else{
                        //didn't hit any objects so continue moving across game board
                        currentField.setFieldEntity(bullet.getChild());
                        bullet.setChild(nextField.getEntity());
                        nextField.setFieldEntity(bullet);
                        bullet.setParent(nextField);
                    }
                }
            }
        }, 0, BULLET_PERIOD);
        return true;
    }
}
