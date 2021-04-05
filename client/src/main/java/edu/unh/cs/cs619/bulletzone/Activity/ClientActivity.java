package edu.unh.cs.cs619.bulletzone.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import edu.unh.cs.cs619.bulletzone.Singletons.Bus;
import edu.unh.cs.cs619.bulletzone.Controls.Controller;
import edu.unh.cs.cs619.bulletzone.Singletons.Poller;
import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Grid.SimGridFacade;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

/**
 * <h1> Client Activity Class! </h1>
 * The Client activity class initializes everything that is needed on the client activity when
 * the game starts, such as handle phone shakes, create and set the grid, get a unique tank id, and
 * handle button clicks. This is all at a broad level, since the actual implementations are called.
 *
 * @author toBeDecided
 * @version 1.0
 * @since 10/31/2018
 */
// Evaluate ways to not use EActivity
@EActivity(R.layout.activity_client)
public class ClientActivity extends Activity {

    private static final String TAG = "ClientSide";

    SimGridFacade myFacade;
    Poller poller;
    Controller controller;
    private String url = "http://stman1.cs.unh.edu:61953/games";
    int joinIdentifier;
    //private String url = "http://10.0.2.2:8080/games";


    @ViewById
    GridView gridView;

    /**
     * This method defines what will be called on app start up.
     * It sets content view and initializes everything that is needed to play the game.
     * @param savedInstanceState This is for proper phone rotations.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent = getIntent();
        joinIdentifier = mIntent.getIntExtra("identifier", 0);
        setContentView(R.layout.activity_client);
        Log.d(TAG, "Milestone_1");
        controller = new Controller(this, url, joinIdentifier);
        initialize();
    }

    /**
     * This method runs when the emulator activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        poller.stopPoller();
    }

    /**
     * This method runs when the emulator activity is stopped
     */
    @Override
    protected void onStop() {
        super.onStop();
        poller.stopPoller();
    }

    /**
     * This method initializes everything that is needed to play the game such as button clicks,
     * shaker listening, getting a unique tank id, and creating/setting the GridView.
     */
    @AfterViews
    void initialize() {
        myFacade = new SimGridFacade(this);
        myFacade.setAdapter(gridView);

        poller = Poller.getInstance(myFacade, this, url);
    }

    /* Evaluate other way for multi button listener or add all buttons to annotation */

    /**
     * This method is an onClickListener that calls a corresponding action for each specific button
     * click that occurs.
     * @param view This is to help get the id of which button was clicked
     */
    @Click({R.id.buttonForward, R.id.leftFire, R.id.rightFire, R.id.forwardFire, R.id.backFire, R.id.buttonBack, R.id.buttonRight, R.id.buttonLeft,
            R.id.buttonLeave, R.id.buttonEject, R.id.buttonEjectSoldier, R.id.vehicleSelect})
    protected void onButtonClick(View view) {
        controller.buttonAction(view.getId(), this);
    }
}
