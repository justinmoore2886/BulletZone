package edu.unh.cs.cs619.bulletzone.Controls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.unh.cs.cs619.bulletzone.Activity.HomeScreenActivity;
import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Singletons.Bus;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

/**
 * <h1> Controller Class! </h1>
 * The controller class implements all the actions that need to occur for the game to be
 * playable. This includes: handle button actions, send those actions to the server, joining and
 * leaving game, and shake actions.
 *
 * @author Luke McIntire
 * @version 1.0
 * @since 10/31/2018
 */
public class Controller {
    private static final String TAG = "ClientSide";
    private RequestQueue queue;
    private long tankID;
    private String url;
    private UserTank userTank;
    private ShakeSensor shaker;
    private Context context;

    private Vibrator vibrator;



    /**
     * This is the constructor. It initializes queue, and url.
     * @param c This is the context.
     *@param u This is the url.
     */
    public Controller(Context c, String u, int identifier) {
        queue = Volley.newRequestQueue(c);
        url = u + "/";
        shaker = getShakeSensor(c);
        shaker.setOnShakeListener(new ShakeSensor.OnShakeListener() {
            @Override
            public void onShake() {
                shakeAction();
            }
        });
        context = c;
        vibrator = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);

        joinGame(identifier);
    }

    /**
     * This is the secondary constructor for testing. It initializes queue to null, and url.
     * @param u This is the url.
     */
    // second constructor primarily for testing without volley
    public Controller(String u) {
        queue = null;
        url = u + "/";
        userTank = UserTank.getInstance(-1);
    }

    /**
     * This method is to concatenate a string so that it can be parsed by the server to allow
     * the following actions to be done.
     * @param button_ID This is to identify which button was clicked.
     */
    public void buttonAction(int button_ID, Context context) {
        int direction = userTank.getDirection();
        boolean send = true;
        boolean directionalFire = false;
        if(userTank.getControlledIdentifier() != 1){directionalFire = true;}
        //tankID = UserTank.getInstance(-1).getTankId();

        // Temporary action url with tank ID
        String action_url = url + Long.toString(tankID);

        // Add action to to action url based on button
        switch (button_ID) {

            // Action for move Forward
            case R.id.buttonForward:
                action_url += "/move/" + String.valueOf(direction);
                Log.d(TAG, "left turn attempt " + action_url + " direction "  + direction);
                break;

            // Action for move backward
            case R.id.buttonBack:
                action_url += "/move/" + String.valueOf((direction + 4) %8);
                break;

            // Action for turn left
            case R.id.buttonLeft:
                if(direction == 0){direction = 6;}
                else if(direction == 2){direction = 0;}
                else if(direction == 4){direction = 2;}
                else if(direction == 6){direction = 4;}
                action_url += "/turn/" + String.valueOf(direction);
                Log.d(TAG, "left turn attempt " + action_url + " direction "  + direction);
                break;

            // Action for turn right
            case R.id.buttonRight:
                direction = (direction + 2) % 8;
                action_url += "/turn/" + String.valueOf(direction);
                break;

            // Action for left fire
            case R.id.leftFire:
                send = directionalFire;
                if(direction == 0){direction = 6;}
                else if(direction == 2){direction = 0;}
                else if(direction == 4){direction = 2;}
                else if(direction == 6){direction = 4;}
                action_url += "/fire/" + direction;
                break;

            // Action for right fire
            case R.id.rightFire:
                send = directionalFire;
                action_url +=  "/fire/" + (direction + 2) % 8;
                break;

            // Action for forward fire
            case R.id.forwardFire:
                //action_url += "/fire/" + direction;
                action_url += "/fire/"+String.valueOf(direction);
                break;

            // Action for back fire
            case R.id.backFire:
                send = directionalFire;
                action_url += "/fire/" + String.valueOf((direction + 4) %8);
                break;

            // Action for leave
            case R.id.buttonLeave:
                leaveGame(action_url + "/leave", context);
                return;

            case R.id.buttonEject:
                action_url += "/eject/1";
                break;

            case R.id.buttonEjectSoldier:
                action_url += "/ejectSoldier/1";
                break;

            case R.id.vehicleSelect:
                if(userTank.setControlled()){
                    if(tankID > 9){tankID = tankID / 10;}
                    else{tankID = tankID*10;}
                }
                Log.e(TAG, "temp tank id is " + tankID);
                send = false;
                break;

            // If incorrect button given
            default:
                vibrateReject();
                Log.e(TAG, "Unknown movement button id: " + button_ID);
                break;
        }

        // Send action to server
        if(send)
            sendAction(action_url);
    }

    /**
     * This method actually creates a PUT request and stores it in a queue so that it can be sent
     * to the server.
     * @param url This is the server url.
     * @exception VolleyError This throws if there is a request error.
     */
    protected void sendAction(String url) {

        if(queue != null) {
            // Volley PUT request
            StringRequest putRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                // On on response from server put request
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response for sendAction Volley request " + response);
                }
            },
                    // On request error
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            vibrateReject();
                            Log.d(TAG, "onErrorResponse() for action Volley PUT request");
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", "Justin");
                    params.put("domain", "http://stman1.cs.unh.edu:61903/games");
                    return params;
                }
            };
            queue.add(putRequest);
        }
    }


    /**
     * This method gets a new shaker given a context.
     * @param context This the context.
     * @return ShakeSensor This returns a new ShakeSensor Object.
     */
    public ShakeSensor getShakeSensor(Context context){
        return new ShakeSensor(context);
    }

    /**
     * This method deems a shake to be equivalent to firing a bullet. It sends this action to the
     * server.
     */
    public void shakeAction(){
        sendAction(url + String.valueOf(tankID) + "/fire/1");
    }

    /**
     * This method allows for the user to join the server / game with a unique ID using a POST request.
     * @exception JSONException This throws if there is a parsing error
     */
    public void joinGame(int identifier) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, url+identifier, new Response.Listener<String>() {
            // On server response
            @Override
            public void onResponse(String response) {

                // Parse Json response into tank ID
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    tankID = Long.valueOf(jsonObject.getString("result"));
                    userTank = UserTank.getInstance(tankID);
                    userTank.createUserViews(context);
                }
                // Throw error if parse failed
                catch (JSONException e) {
                    e.printStackTrace();
                }

                userTank.setTankId(tankID);
                Log.d(TAG, "Response for joinGame() is " + String.valueOf(tankID));
            }
        },
                // If Volley request error
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        vibrateReject();
                        Log.d(TAG, "onErrorResponse() for initial Volley POST");
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                return params;
            }
        };
        queue.add(postRequest);
    }

    /**
     * This method is for leaving a game/server. It basically puts in a DELETE request to the server.
     * @param url This is the server url.
     * @exception VolleyError This throws if there is a request error.
     */
    protected void leaveGame(String url, Context context) {

        if(queue != null) {
            // Volley Delete Request
            StringRequest putRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

                // On server response
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response for leaveGame() is " + response);
                }
            },
                    // On error response
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            vibrateReject();
                            Log.d(TAG, "onErrorResponse() for Leave Game");
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", "Justin");
                    params.put("domain", "http://stman1.cs.unh.edu:61903/games");
                    return params;
                }
            };
            queue.add(putRequest);
        }

        // Go back to the home screen
        userTank.setInstanceToNull();
        Bus.getInstance().setInstanceToNull();
        Intent intent = new Intent(context, HomeScreenActivity.class);
        context.startActivity(intent);
    }

    /**
     * This method vibrates the phone when the user does something the server doesn't allow
     */
    protected void vibrateReject(){
        if(vibrator.hasVibrator()){
            vibrator.vibrate(500);
        }
    }
}
