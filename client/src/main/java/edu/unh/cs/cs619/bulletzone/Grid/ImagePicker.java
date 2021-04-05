package edu.unh.cs.cs619.bulletzone.Grid;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

/**
 * <h1> ImagePicker Class! </h1>
 * The controller class basically just assigns an image based on the gridValue that exists.
 *
 * @author Justin Moore
 *
 * @version 1.0
 * @since 10/31/2018
 */
public class ImagePicker {

    // Instance has already been created in Controller, so we just pass in dummy id & get real tank
    private Drawable health;
    private UserTank userTank;
    private Context context;

    /**
     * User Tank
     */
    private int[] tank1_full_bank = new int[] {
            R.drawable.tank1_up_full, 0, R.drawable.tank1_right_full, 0,
            R.drawable.tank1_down_full, 0, R.drawable.tank1_left_full
    };

    private int[] tank1_medium_bank = new int[] {
            R.drawable.tank1_up_medium, 0, R.drawable.tank1_right_medium, 0,
            R.drawable.tank1_down_medium, 0, R.drawable.tank1_left_medium
    };

    private int[] tank1_low_bank = new int[] {
            R.drawable.tank1_up_low, 0, R.drawable.tank1_right_low, 0,
            R.drawable.tank1_down_low, 0, R.drawable.tank1_left_low
    };

    /**
     * Enemy Tanks
     */
    private int[] tank2_full_bank = new int[] {
            R.drawable.tank2_up_full, 0, R.drawable.tank2_right_full, 0,
            R.drawable.tank2_down_full, 0, R.drawable.tank2_left_full
    };

    private int[] tank2_medium_bank = new int[] {
            R.drawable.tank2_up_medium, 0, R.drawable.tank2_right_medium, 0,
            R.drawable.tank2_down_medium, 0, R.drawable.tank2_left_medium
    };

    private int[] tank2_low_bank = new int[] {
            R.drawable.tank2_up_low, 0, R.drawable.tank2_right_low, 0,
            R.drawable.tank2_down_low, 0, R.drawable.tank2_left_low
    };

    /**
     * Empty Tanks
     */
    private int[] tank3_bank = new int[] {
            R.drawable.tank3_up,    0,
            R.drawable.tank3_right, 0,
            R.drawable.tank3_down,  0,
            R.drawable.tank3_left
    };

    private int[] ship1_bank = new int[] {
            R.drawable.ship1_up,    0,
            R.drawable.ship1_right, 0,
            R.drawable.ship1_down,  0,
            R.drawable.ship1_left
    };


    private int[] ship2_bank = new int[] {
            R.drawable.ship2_up,    0,
            R.drawable.ship2_right, 0,
            R.drawable.ship2_down,  0,
            R.drawable.ship2_left
    };

    private int[] ship3_bank = new int[] {
            R.drawable.ship3_up,    0,
            R.drawable.ship3_right, 0,
            R.drawable.ship3_down,  0,
            R.drawable.ship3_left
    };

    /**
     * User Soldier
     */
    private int[] soldier1_bank = new int[] {
            R.drawable.soldier1_up,    0,
            R.drawable.soldier1_right, 0,
            R.drawable.soldier1_down,  0,
            R.drawable.soldier1_left
    };

    /**
     * User Soldier Wheeled
     */
    private int[] segway_soldier1_bank = new int[] {
            R.drawable.soldier1_wheel_up,    0,
            R.drawable.soldier1_wheel_right, 0,
            R.drawable.soldier1_wheel_down,  0,
            R.drawable.soldier1_wheel_left
    };


    /**
     * Enemy Soldiers
     */
    private int[] soldier2_bank = new int[] {
            R.drawable.soldier2_up,    0,
            R.drawable.soldier2_right, 0,
            R.drawable.soldier2_down,  0,
            R.drawable.soldier2_left
    };

    /**
     * Enemy Soldier Wheeled
     */
    private int[] segway_soldier2_bank = new int[] {
            R.drawable.soldier2_wheel_up,    0,
            R.drawable.soldier2_wheel_right, 0,
            R.drawable.soldier2_wheel_down,  0,
            R.drawable.soldier2_wheel_left
    };

    /**
     *  Destructible Walls
     */
    private int[] wall_bank = new int[] {
            R.drawable.blank, R.drawable.destructible_wall_low,
            R.drawable.destructible_wall_medium,
            R.drawable.destructible_wall_full,
    };


    private int[] terrain_bank = new int[] {
            R.drawable.blank,
            R.drawable.coast,
            R.drawable.debris,
            R.drawable.hill,
            R.drawable.water,
    };

    /**
     *  Bullets
     */
    private int[] bullet_bank = new int[] {
            R.drawable.bullet_up,    0,
            R.drawable.bullet_right, 0,
            R.drawable.bullet_down,  0,
            R.drawable.bullet_left
    };

    private int[] health_bank = new int[] {
            R.drawable.health_transparent20, R.drawable.health_transparent19,
            R.drawable.health_transparent18, R.drawable.health_transparent17,
            R.drawable.health_transparent16, R.drawable.health_transparent15,
            R.drawable.health_transparent14, R.drawable.health_transparent13,
            R.drawable.health_transparent12, R.drawable.health_transparent11,
            R.drawable.health_transparent10, R.drawable.health_transparent9,
            R.drawable.health_transparent8,  R.drawable.health_transparent7,
            R.drawable.health_transparent6,  R.drawable.health_transparent5,
            R.drawable.health_transparent4,  R.drawable.health_transparent3,
            R.drawable.health_transparent2,   R.drawable.health_transparent1,
            R.drawable.health_transparent
    };

    private Drawable getControlImage(Context context, Drawable image){
        Drawable[] layers = new Drawable[2];
        layers[0] = image;
        layers[1] = context.getResources().getDrawable(R.drawable.controlled);
        return new LayerDrawable(layers);
    }

    private Drawable getHealth(int h, int identifier, Context context ){
        int tempHealth = h;
        if(identifier == 2){ tempHealth = (h/5)*4; }
        else{ tempHealth = h/5; }
         //Log.e("usertank", "temp health " + tempHealth + "\n");
        Drawable healthBar = context.getResources().getDrawable(health_bank[tempHealth]);
        //(ClipDrawable) soldierImg.getDrawable();
        //Drawable healthBar = context.getResources().getDrawable(R.drawable.health_transparent)
        //healthBar.setLevel(tempHealth);
        return healthBar;
    }

    private Drawable getImage(int h, int identifier, Context context, Drawable image, int terrain){
        Drawable[] layers = new Drawable[2];
        layers[0] = getTerrain(image, terrain, context);
        layers[1] = getHealth(h, identifier, context);
        return new LayerDrawable(layers);
    }

    private Drawable getTerrain(Drawable image, int terrain,  Context context){
        Drawable[] layers = new Drawable[2];
        layers[0] = context.getResources().getDrawable( terrain_bank[terrain]);
        layers[1] = image;
        return new LayerDrawable(layers);
    }

    private int getUserTankDirection(int tankHealth, int tankDirection) {
        if(tankHealth >= 70)
            return tank1_full_bank[tankDirection];
        else if(tankHealth >= 40)
            return tank1_medium_bank[tankDirection];
        else
            return tank1_low_bank[tankDirection];
    }

    private int getEnemyTankDirection(int tankLifeLeft, int tankDirection) {
        if(tankLifeLeft >= 70)
            return tank2_full_bank[tankDirection];
        else if(tankLifeLeft >= 40)
            return tank2_medium_bank[tankDirection];
        else
            return tank2_low_bank[tankDirection];
    }

    /**
     * This method picks an image based on the value that is received from server, whether that be a bullet,
     * tank, etc.
     *
     * @param gridValue This is the value used to assign image
     * @param aContext Context of game
     * @return int This returns an R.Drawable image id which is an int.
     */
    protected Drawable pickImage(int gridValue, Context aContext) {
         userTank = UserTank.getInstance(-1);
         context = aContext;
        if(gridValue == 0)
            return context.getResources().getDrawable( R.drawable.blank);

        else if(gridValue == 1000)
            return context.getResources().getDrawable( R.drawable.indestructible_wall);

        else if(gridValue > 1000 && gridValue < 2000) {
            int wallHealth = gridValue % 10;
            return context.getResources().getDrawable( wall_bank[wallHealth]);
        }

        else if(gridValue > 2000000 && gridValue < 3000000) {
            int terrain = gridValue/100000%10;
            int bulletDirection = gridValue % 10;
            return getTerrain(context.getResources().getDrawable( bullet_bank[bulletDirection]), terrain, context);
        }
        else if(gridValue == 4500003){
            return context.getResources().getDrawable( R.drawable.hill);
        }
        else if(gridValue == 4600002){
            return context.getResources().getDrawable( R.drawable.fusion_reactor);
        }
        else if(gridValue == 4500002){
            return context.getResources().getDrawable( R.drawable.debris);
        }
        else if(gridValue == 4600001){
            return context.getResources().getDrawable( R.drawable.anti_gravity);
        }
        else if(gridValue == 4500004){
            return context.getResources().getDrawable( R.drawable.water);
        }
        else if(gridValue == 4600003){
            return context.getResources().getDrawable( R.drawable.power_rack);
        }
        else if(gridValue == 4500001){
            return context.getResources().getDrawable( R.drawable.coast);
        }
        else if(gridValue > 10000000 && gridValue <= 600000000) {
            int terrain = gridValue/100000000;
            gridValue = gridValue%100000000;
            int tankId = gridValue / 10000 % 100;
            int tankLifeLeft = gridValue / 10 % 1000;  // Direction: 0 = UP, 2 = RIGHT, 4 = DOWN, 6 = LEFT
            int tankDirection = gridValue % 10;
            int identifier = gridValue/10000000;
            int powerUp = gridValue/1000000%10;
            if(powerUp > 5){
                powerUp -=5;
                gridValue = 60000000;
            }
            Drawable image;

            // It's the users tank
            if (tankId == userTank.getTankId()) {
                Log.d("image picker", "powerup " + powerUp + " id " + identifier + " grid val " + gridValue + " ");
                userTank.setPrimary(identifier);
                userTank.setPrimaryHealth(tankLifeLeft);
                //Log.d("image picker", "left turn direction update " + tankDirection + " id " + tankId);
                userTank.setDirection(tankDirection, tankId);
                userTank.setPrimaryPowerUp(powerUp);

                if (gridValue < 20000000)
                    image =  context.getResources().getDrawable( getUserTankDirection(tankLifeLeft, tankDirection));
                else if(gridValue == 60000000)
                    image = context.getResources().getDrawable( segway_soldier1_bank[tankDirection]);
                else if (gridValue < 50000000 && gridValue > 30000000)
                    image =  context.getResources().getDrawable( ship1_bank[tankDirection]);
                else
                    image =  context.getResources().getDrawable( soldier1_bank[tankDirection]);
            }

            //vehicle controlled by user's powerup
            else if(tankId == 10*userTank.getTankId()){
                userTank.setRemote(identifier);
                userTank.setremoteHealth(tankLifeLeft);
                userTank.setDirection(tankDirection, tankId);
                userTank.setRemotePowerUp(powerUp);

                if (gridValue < 20000000)
                    image =  getControlImage(context, context.getResources().getDrawable( getUserTankDirection(tankLifeLeft, tankDirection)));
                else if(gridValue == 60000000)
                    image = context.getResources().getDrawable( segway_soldier2_bank[tankDirection]);
                else if (gridValue < 50000000 && gridValue > 30000000)
                    image =  getControlImage(context, context.getResources().getDrawable( ship1_bank[tankDirection]));
                else if (gridValue >= 50000000)
                    image =  getControlImage(context, context.getResources().getDrawable( ship3_bank[tankDirection]));
                else
                    image =  getControlImage(context, context.getResources().getDrawable( soldier1_bank[tankDirection]));
            }

            // It's the enemies tank
            else {
                //Log.d("image picker", "empty tank " + gridValue);
                if(gridValue >= 30000000 && gridValue < 40000000)
                    image =  context.getResources().getDrawable( tank3_bank[tankDirection]);
                else if (gridValue < 50000000 && gridValue >= 40000000)
                    image =  context.getResources().getDrawable( ship2_bank[tankDirection]);
                else if(gridValue == 60000000)
                    image = context.getResources().getDrawable( segway_soldier2_bank[tankDirection]);
                else if(gridValue < 20000000)
                    image =  context.getResources().getDrawable( getEnemyTankDirection(tankLifeLeft, tankDirection));
                else if(gridValue >= 20000000 && gridValue <30000000)
                    image =  context.getResources().getDrawable( soldier2_bank[tankDirection]);
                else{
                    image = context.getResources().getDrawable( ship3_bank[tankDirection]);
                }
            }
            return getImage(tankLifeLeft, identifier, context, image, terrain);
        }
        else
            return context.getResources().getDrawable( R.drawable.blank);
    }
}