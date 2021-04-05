package edu.unh.cs.cs619.bulletzone.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.ImageView;

import org.greenrobot.eventbus.Subscribe;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Singletons.Bus;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

public class ViewUpdate {
    Context context;
    UserTank userTank;
    ClipDrawable tankImageDrawable;
    ClipDrawable soldierImageDrawable;
    private Drawable[] vehicleSelectButtons;
    private Drawable[] powerUpEjects;

    public ViewUpdate(Context acontext){
        context = acontext;

        ImageView tankImg = (ImageView) ((Activity) context).findViewById(R.id.imageView1);
        ImageView soldierImg = (ImageView) ((Activity) context).findViewById(R.id.imageView2);
        tankImageDrawable = (ClipDrawable) tankImg.getDrawable();
        soldierImageDrawable = (ClipDrawable) soldierImg.getDrawable();

        vehicleSelectButtons = new Drawable[] {
                context.getResources().getDrawable(R.drawable.ball),
                context.getResources().getDrawable(R.drawable.tank_selected),
                context.getResources().getDrawable(R.drawable.soldier_selected),
                context.getResources().getDrawable(R.drawable.ball),
                context.getResources().getDrawable(R.drawable.ship_selected)
        };

        powerUpEjects = new Drawable[] {
                context.getResources().getDrawable(R.drawable.empty_eject),
                context.getResources().getDrawable(R.drawable.anti_gravity_eject),
                context.getResources().getDrawable(R.drawable.fusion_reactor_eject),
                context.getResources().getDrawable(R.drawable.power_rack_eject),
                context.getResources().getDrawable(R.drawable.remote_control_eject)
        };
    }
    //health

    /**
     * This method shows the health for entities
     * @param health current health
     * @param identifier identifier of tank/soldier/etc
     * @param primaryHealth primary health of entity
     */
    public void showHealth(int health, int identifier, int primaryHealth, int remoteHealth) {
        //TODO add health for ship
        int tempHealth = health;
        if(identifier == 2){tempHealth = health*400;}
        if(identifier != 2){tempHealth = health*100;}
        if(UserTank.getInstance(-1).getControlled() == 1 && identifier == 1){tankImageDrawable.setLevel(tempHealth);
            soldierImageDrawable.setLevel(10000);}
        else if(UserTank.getInstance(-1).getControlled() == 1 && identifier == 2){tankImageDrawable.setLevel(remoteHealth*100);
            soldierImageDrawable.setLevel(primaryHealth*400);}
        if(primaryHealth == 0){
            tankImageDrawable.setLevel(0);
            soldierImageDrawable.setLevel(0);
        }
        else if(UserTank.getInstance(-1).getControlled() == 2){soldierImageDrawable.setLevel(primaryHealth*100);
            tankImageDrawable.setLevel(tempHealth);
        }
    }

    /**
     * This method shows the select button
     * @param identifier identifier of tank/soldier/etc
     */
    public void showSelectButton(int identifier){
        Drawable image = vehicleSelectButtons[identifier];
        //Log.d("bob", Integer.toString(UserTank.getInstance(-1).getControlled()));
        if(UserTank.getInstance(-1).getControlled() == 2) {
            Drawable[] layers = new Drawable[2];
            layers[0] = image;
            layers[1] = context.getResources().getDrawable(R.drawable.selector_controlled);
            image = new LayerDrawable(layers);
        }

        ((Activity) context).findViewById(R.id.vehicleSelect).setBackground(image);

        //TODO add select button for ship
        if(identifier == 1){
            ((Activity) context).findViewById(R.id.leftFire).setBackground(context.getResources().getDrawable(R.drawable.left_fire_locked));
            ((Activity) context).findViewById(R.id.backFire).setBackground(context.getResources().getDrawable(R.drawable.backward_fire_locked));
            ((Activity) context).findViewById(R.id.rightFire).setBackground(context.getResources().getDrawable(R.drawable.right_fire_locked));
        }else{
            ((Activity) context).findViewById(R.id.leftFire).setBackground(context.getResources().getDrawable(R.drawable.click_fire_left));
            ((Activity) context).findViewById(R.id.backFire).setBackground(context.getResources().getDrawable(R.drawable.click_fire_back));
            ((Activity) context).findViewById(R.id.rightFire).setBackground(context.getResources().getDrawable(R.drawable.click_fire_right));
        }
    }


    public void showPowerUp(int powerUp, int i){
        if(userTank.getInstance(-1).getControlled() == i ) {
            ((Activity) context).findViewById(R.id.powerUp).setBackground(powerUpEjects[powerUp]);
        }
    }
}
