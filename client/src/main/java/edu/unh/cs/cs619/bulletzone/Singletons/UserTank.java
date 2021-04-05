package edu.unh.cs.cs619.bulletzone.Singletons;

import android.content.Context;
import android.util.Log;

import edu.unh.cs.cs619.bulletzone.Activity.ViewUpdate;

/**
 * <h1> UserTank Class! </h1>
 * The UserTank class is a singleton that gives everyone access to tankId and direction.
 * @author Justin Moore
 * @version 1.0
 * @since Halloween!
 *
 */
public class UserTank {

    private static UserTank userTankInstance = null;
    private long tankId;
    private int remoteDirection;
    private int primaryDirection;
    private int primaryHealth;
    private int remoteHealth;
    private int remotePowerUp;
    private int primaryPowerUp;
    private String CurrentPowerUp;
    //index into vehicles for vehicle currently controlled
    private int controlled;
    private ViewUpdate userView = null;
    //stores identifiers for controlled vehicles
    //first index is primary, second index is remote controlled
    private int[] vehicles= {0,0,0};

    /**
     * This is the constructor. It sets the tankId and direction on join game
     * @param id This is the tankId from joinGame()
     */
    private UserTank(long id) {
        tankId = id;
        //Log.d("usertank", "set tank id" + tankId);
        CurrentPowerUp = "";
        remoteDirection = 0;
        primaryDirection = 0;
        primaryHealth = 100;
        remoteHealth = 25;
        controlled = 1;
    }

    public void createUserViews(Context context){
        userView = new ViewUpdate(context);
    }

    /**
     * This method allows for one instance to be maintained AKA singleton
     */
    public static UserTank getInstance(long id) {
        if (userTankInstance == null)
            userTankInstance = new UserTank(id);

        return userTankInstance;
    }

    /**
     * getter for tankId
     * @return tankId This is the tankId
     */
    public long getTankId() {
        return tankId;
    }

    /**
     * setter for tankId
     *  @param i This is the ID for the tank
     */
    public void setTankId(long i) {
        tankId = i;
    }

    /**
     * getter for direction
     * @return direction This is the direction for bullets and tanks
     */
    public int getDirection() {
        if(controlled == 1){Log.d("userTank", "left turn direction update " + primaryDirection + " id " + tankId); return primaryDirection;}
        return remoteDirection;
    }

    /**
     * setter for direction
     * @param d This is the direction for bullets and tanks.
     */
    public void setDirection(int d, int id) {
        if(id < 10){primaryDirection = d;}
        else{remoteDirection = d;}
        //Log.d("image picker", "left turn direction update " + d + " id " + tankId + " remotedirection " + remoteDirection + " primary direction " + primaryDirection);
    }

    public int getRemoteHealth(){
        return remoteHealth;
    }

    public void setRemotePowerUp(int p){
        remotePowerUp = p;
        userView.showPowerUp(p, 2);
    }

    public void setPrimaryPowerUp(int p){
        primaryPowerUp = p;
        userView.showPowerUp(p, 1);
    }

    public void setremoteHealth(int h) {
        if(userView != null) {
            if (h != remoteHealth) {
                userView.showHealth(h, vehicles[2], primaryHealth, remoteHealth);
            }
        }
        remoteHealth = h;
    }

    public void setPrimaryHealth(int h) {
        primaryHealth = h;
        if(userView != null) {
            userView.showHealth(h, vehicles[1], primaryHealth, remoteHealth);
        }
    }

    public void setPrimary(int identifier) {
        if (userView != null) {
            //setPrimaryHealth(0);
            //setremoteHealth(0);
            //vehicles[2]=0;
            vehicles[1] = identifier;
            if (controlled == 1) {
                userView.showSelectButton(vehicles[controlled]);
            }
        }
    }

    public void setRemote(int identifier){
        vehicles[2] = identifier;
        /*if(controlled == 1) {
        }*/
    }

    public boolean setControlled(){
        Log.e("usertank", "controlled and id of controlled " + controlled +" " + vehicles[controlled]);
       //Log.e("usertank", "controlled and id of controlled 2nd " + controlled +1 +" " + vehicles[controlled+1]);
        int tempControlled = controlled+1;
        if(tempControlled > 2){tempControlled = 1;}
        if(vehicles[tempControlled] != 0) {
            controlled = tempControlled;
            Log.e("usertank", "controlled and id of controlled " + controlled +" " + vehicles[controlled]);
            if (userView != null) {
                userView.showSelectButton(vehicles[controlled]);
            }
            return true;
        }
        return false;
    }

    //returns identifier of currently controlled vehicle
    public int getControlledIdentifier(){
        return vehicles[controlled];
    }

    public int getControlled(){
        return controlled;
    }

    /**
     * Tank singleton has to be set to null when leaving because it stores the old tankID and
     * you're given a new tankID in joinGame() when re-entering the game after leaving
     */
    public void setInstanceToNull() {
        userTankInstance = null;
    }

    public void setCurrentPowerUp(int p){ }

    public void getCurrentPowerUp(int p){ }

    public void didIDie(int alive, int id){
        if(id == tankId && alive == 0){
            //Log.e("usertank", "event id " + id + "\n");
            setPrimaryHealth(0);
            setremoteHealth(0);
        }
        if(id == tankId*10 && alive == 0){
            //setremoteHealth(0);
            setRemote(0);
            if(controlled == 2){setControlled();}
        }
    }
}
