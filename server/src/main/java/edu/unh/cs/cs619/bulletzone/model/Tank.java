package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;
import java.util.Stack;

import edu.unh.cs.cs619.bulletzone.model.powerups.PowerRack;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerUp;
import edu.unh.cs.cs619.bulletzone.model.powerups.RemoteControl;
import edu.unh.cs.cs619.bulletzone.model.terrain.Coast;
import edu.unh.cs.cs619.bulletzone.model.terrain.Empty;

/**
 * <h1> Tank Class! </h1>
 * This class handles the tank creation and hit actions along with its constraints.
 * @author TBD
 * @version 1.0
 * @since Halloween
 */
public class Tank extends FieldEntity {

    private static final String TAG = "GameObject";

    private long id;

    private final String ip;

    private long lastMoveTime;
    private int allowedMoveInterval;

    private long lastFireTime;
    private int allowedFireInterval;
    private int allowedShipSideFireInterval;

    private int numberOfBullets;
    private int allowedNumberOfBullets;

    private long lastEnterTime;
    private int allowedReEnterTime;

    private long lastTurnTime;
    private int allowedTurnInterval;

    private long controllerId = -1;
    private long ownerId=-1;
    private long controlsId=-1;

    private int terrain=0;
    private int powerup=0;
    private int soldierRack;
    private int allowedPowerUps=1;
    private int life;
    private int identifier;
    public PowerUp powerUps = new PowerUp();
    RemoteControl remote;
    private Direction direction;
    private Bullet bullets[] = {null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
    public PowerRack rack = new PowerRack(getPos());

    /**
     * This is the constructor. It initializes time variables and direction/tankId
     * @param id this is the tankId
     * @param direction this is the direction of tanks/bullets
     * @param ip This is the server
     */
    public Tank(long id, Direction direction, String ip, int identifier) {
        this.id = id;
        ownerId = id;
        this.direction = direction;
        this.ip = ip;
        this.identifier = identifier;
        numberOfBullets = 0;
        soldierRack=0;
        lastFireTime = 0;
        lastMoveTime = 0;
        lastEnterTime = 0;
        lastTurnTime = 0;
        allowedReEnterTime = TankInfo.allowedReEnterTime;
        allowedShipSideFireInterval = ShipInfo.allowedSideFireInterval;
        if(identifier == 4) {
            allowedNumberOfBullets = ShipInfo.allowedNumberOfBullets;
            allowedFireInterval = ShipInfo.allowedMainFireInterval;
            allowedMoveInterval = ShipInfo.allowedMoveInterval;
            allowedTurnInterval = ShipInfo.allowedTurnInterval;
            life = ShipInfo.life;
        }else if(identifier == 2){
            allowedNumberOfBullets = SoldierInfo.allowedNumberOfBullets;
            allowedFireInterval = SoldierInfo.allowedFireInterval;
            allowedMoveInterval = SoldierInfo.allowedMoveInterval;
            allowedTurnInterval = SoldierInfo.allowedTurnInterval;
        }else {
            allowedNumberOfBullets = TankInfo.allowedNumberOfBullets;
            allowedFireInterval = TankInfo.allowedFireInterval;
            allowedMoveInterval = TankInfo.allowedMoveInterval;
            life = TankInfo.life;
        }
    }

    /**
     * This method creates a new tank with the same attributes
     * @return Tank Object
     */
    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip, identifier);
    }


    /**
     * This method is what manages health and death for tanks
    // * @param damage This is the damage that occurs to the tank
     */
    @Override
    public boolean hit(Bullet bullet) {
        life = life - bullet.getDamage();
        System.out.println("Tank life: " + id + " : " + life);
//		Log.d(TAG, "TankId: " + id + " hit -> life: " + life);
        return false;
    }
    public void setId(long Id){id = Id;}

    public PowerRack getPowerUpStack(){
        return rack;
    }

    public void setPowerUpStack(PowerRack pRack){
        rack = pRack;
        rack.rackIndicator = true;
    }

    public void setRemote(RemoteControl Remote){remote = Remote;}

    public RemoteControl getRemote(){return remote;}

    public int getAllowedShipSideFireInterval(){return allowedShipSideFireInterval;}

    public void setAllowedShipSideFireInterval(int allowedShipSideFireInterval){this.allowedShipSideFireInterval = allowedShipSideFireInterval;}

    public int getAllowedTurnInterval(){return allowedTurnInterval;}

    public void setAllowedTurnInterval(int allowedTurnInterval){this.allowedTurnInterval = allowedTurnInterval;}

    public long getLastTurnTime(){return lastTurnTime;}

    public void setLastTurnTime(long lastTurnTime){this.lastTurnTime = lastTurnTime;}

    public int getIdentifier(){return identifier;}

    public void setIdentifier(int identifier){this.identifier = identifier;}

    public int getAllowedReEnterTime(){return allowedReEnterTime;}

    public void setAllowedReEnterTime(int allowedReEnterTime){this.allowedReEnterTime = allowedReEnterTime;}

    public long getLastEnterTime(){return lastEnterTime;}

    public void setLastEnterTime(long lastEnterTime){this.lastEnterTime = lastEnterTime;}

    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public long getAllowedMoveInterval() {
        return allowedMoveInterval;
    }

    public void setAllowedMoveInterval(int allowedMoveInterval) { this.allowedMoveInterval = allowedMoveInterval; }

    public long getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public long getAllowedFireInterval() {
        return allowedFireInterval;
    }

    public void setAllowedFireInterval(int allowedFireInterval) {
        this.allowedFireInterval = allowedFireInterval;
    }

    public void addBullet(Bullet bullet, int index){
        bullets[index]=bullet;
    }
    public Bullet getBullet(int bulletNumber){
        return bullets[bulletNumber];
    }

    public void clearBullets(){
        //sets each bullet's damage to a 'clear' state of 0 damage
        for(int i=0; i<16; i++){
            if(getBullet(i) != null) {
                getBullet(i).setDamage(0);
                System.out.println("bullet damage going to zero" + getBullet(i).getBulletId());
            }
        }
    }

    public int getNumberOfBullets() {
        return numberOfBullets;
    }

    public void setPowerUpInt(int p){powerup = p;}

    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }

    public int getAllowedNumberOfBullets() {
        return allowedNumberOfBullets;
    }

    public void setAllowedNumberOfBullets(int allowedNumberOfBullets) {
        this.allowedNumberOfBullets = allowedNumberOfBullets;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    /**
     * This method creates a unique parsable value
     * @return int that will be parsed
     */
    @Override
    public int getIntValue() {
        int intValue = 1000000000;
        //return (int) (10000000 * identifier + 100000 * id + 10 * life + Direction ----to allow for ids greater than 9
        return (int) (/*intValue*soldierRack*/ + 100000000*terrain+ 10000000*identifier + 1000000*(powerup+soldierRack) + 10000 * id + 10 * life + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public String getIp(){
        return ip;
    }

    public void setTerrain(int interval){
        this.setAllowedMoveInterval(interval);
    }

    public void setChild(FieldEntity child) {
        if(child == null){
            this.child = new Empty();

            /*for(int i = 128; i < 145; i++){
                if(this.getPos() == i){
                    this.child = new Coast(i);
                }
            }
            for(int i = 176; i < 193; i++){
                if(this.getPos() == i){
                    this.child = new Coast(i);
                }
            }*/
            return;
        }
        if(child.getIntValue()/100000%10 == 5) {terrain = child.getIntValue() % 10; }
        else{terrain = 0;} // comment this out and it start
        this.child = child;
    }

    public void setPowerup(int p){powerup = p;}

    public void setSoldierRacked(int r){soldierRack = r;}

    public long getControllerId() {return controllerId; }

    public long getOwnerId(){return ownerId;}

    public long getControlsId(){return controlsId;}

    public void setControls(long controls){controlsId = controls;}

    public void setOwnerId(long ownerID){ownerId = ownerID;}

    public void setControllerId(long controller){controllerId = controller;}

    public void setPowerUp(PowerUp powerUp){
        //if it is any powerup, set the powerup for the first time and apply effect
        if(!rack.rackIndicator) {
            if(remote == null || remote.getEjected()) {
                powerUps = powerUp;
                powerUp.applyEffect(this);
            }
        }
        else { // otherwise add it to the rack and apply the most recent effect
            rack.RackAddition(powerUp, this.getPowerUpStack());
            rack.applyEffect(this);
        }
        powerup = powerUp.getIntValue()%10;
    }

    public PowerUp getPowerUp(){
        return powerUps;
    }

    public void SpawnRack(Tank tank){
        byte i = 0;
        FieldHolder parent = tank.getParent();
        while(i <= 6){
            FieldHolder nextField = parent.getNeighbor(Direction.fromByte(i));
            FieldEntity child = nextField.getEntity();
            if ((nextField.getEntity().toString() == "E") || (nextField.getEntity().toString() == "C") || (nextField.getEntity().toString() == "H") || (nextField.getEntity().toString() == "D")) {
                //FieldEntity FE = nextField.getEntity().getChild();
                //System.out.println("child: " + FE);
                nextField.clearField();
                nextField.setFieldEntity(new PowerRack(tank.getPos()));
                nextField.getEntity().setChild(child);
                //nextField.getEntity().setChild(FE);
                return;
            } else {
                i += 2;
            }
        }
        System.out.println("Should have ejected a rack");
    }

    public boolean ejectPowerUp(){ // returns true if ejected a power rack
        // if the tank has a power rack
        if(powerUps instanceof PowerRack){
            if(rack.rack.empty()) { // if that rack is empty, eject it
                if (getIdentifier() == 2) {setSoldierRacked(0);System.out.println("set soldier racked ");}
                System.out.println("ejected " + powerUps);
                powerUps = new PowerUp();
                rack.rackIndicator = false;
                powerup = 0;
                //this.SpawnRack(this);
                this.setAllowedMoveInterval(500);
                if(this.getIdentifier() == 4) {
                    this.setAllowedFireInterval(ShipInfo.allowedMainFireInterval);
                    this.setAllowedShipSideFireInterval(ShipInfo.allowedSideFireInterval);
                    this.setAllowedNumberOfBullets(ShipInfo.allowedNumberOfBullets);
                    this.setAllowedMoveInterval(ShipInfo.allowedMoveInterval);
                    this.setAllowedTurnInterval(ShipInfo.allowedTurnInterval);
                }else if(this.getIdentifier() == 2){
                    this.setAllowedFireInterval(SoldierInfo.allowedFireInterval);
                    this.setAllowedNumberOfBullets(SoldierInfo.allowedNumberOfBullets);
                    this.setAllowedMoveInterval(SoldierInfo.allowedMoveInterval);
                }else {
                    this.setAllowedFireInterval(TankInfo.allowedFireInterval);
                    this.setAllowedNumberOfBullets(TankInfo.allowedNumberOfBullets);
                    this.setAllowedMoveInterval(TankInfo.allowedMoveInterval);
                }
                return true;
            }
            else { // eject something from the rack (could still eject a different power rack)
                boolean isRack = rack.RackEjection(rack);
                powerup = rack.getCurrentPowerup(rack).getIntValue()%10;
                if(isRack)
                    return true;
                else
                    return false;
            }
        }
        else { // eject the other power-up (not a power rack)
            System.out.println("ejected " + powerUps);
            powerUps.eject();
            if(getRemote() != null){
            if(!getRemote().getEjected()){
                getRemote().eject();}
            }

            powerUps = new PowerUp();
            if(this.getIdentifier() == 4) {
                this.setAllowedFireInterval(ShipInfo.allowedMainFireInterval);
                this.setAllowedNumberOfBullets(ShipInfo.allowedNumberOfBullets);
                this.setAllowedMoveInterval(ShipInfo.allowedMoveInterval);
                this.setAllowedTurnInterval(ShipInfo.allowedTurnInterval);
            }else if(this.getIdentifier() == 2){
                this.setAllowedFireInterval(SoldierInfo.allowedFireInterval);
                this.setAllowedNumberOfBullets(SoldierInfo.allowedNumberOfBullets);
                this.setAllowedMoveInterval(SoldierInfo.allowedMoveInterval);
            }else {
                this.setAllowedFireInterval(TankInfo.allowedFireInterval);
                this.setAllowedNumberOfBullets(TankInfo.allowedNumberOfBullets);
                this.setAllowedMoveInterval(TankInfo.allowedMoveInterval);
            }
            powerup = getPowerUp().getIntValue()%10;
            return false;
        }
    }



    public boolean action(Tank vehicle){
        if((identifier == 3 || identifier == 5) && vehicle.getIdentifier() == 2){
            if(vehicle.getPowerUp() instanceof PowerRack) {
                vehicle.powerUps = new PowerUp();
                vehicle.rack.rackIndicator = false;
                vehicle.SpawnRack(vehicle);
                if (vehicle.getIdentifier() == 2) {vehicle.setSoldierRacked(0);System.out.println("set soldier racked ");}
            }
        }
        //if identifier is active tank and vehicle identifier is soldier                ///TODO
        if((identifier == 1 || identifier == 4) && vehicle.getIdentifier() == 2) {
            //if active tank is controlled, then eject all of its powerups
            if (controllerId > -1) {
                if(!rack.rackIndicator) {
                    ejectPowerUp();
                }
                getRemote().eject();
                vehicle.lastEnterTime = System.currentTimeMillis() + vehicle.getAllowedMoveInterval();
                System.out.println("eject all powerups new identifier" + getIdentifier() + "\n");
            }

            //if vehicle's control id is > -1, return false ---soldier is already controlling something
            else if (vehicle.getControlsId() > -1) {
                return false;
            } else {
                //add control power up to tank power up array
                RemoteControl remote = new RemoteControl(getPos());
                remote.setEffect(vehicle);
                setPowerUp(remote);
                setRemote(remote);
            }
        }
        return false;}

}
