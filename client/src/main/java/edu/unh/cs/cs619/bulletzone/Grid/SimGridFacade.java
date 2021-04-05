package edu.unh.cs.cs619.bulletzone.Grid;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import android.widget.GridView;
import android.widget.ImageView;

import edu.unh.cs.cs619.bulletzone.Singletons.Bus;
import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

/**
 * <h1> SimGridFacade Class! </h1>
 * The SimGridFacade class masks interaction of multiple classes by allowing them a liaison
 * of a Facade.
 *
 * @author Justin Moore
 * @version 1.0
 * @since 10/31/2018
 */
public class SimGridFacade {

    private static final String TAG = "ClientSide";

    public SimulationGrid simulationGrid = new SimulationGrid(); // Stores an array of grid cells
    private GridAdapter gridAdapter;                             // Displays grid
    private GridView gridView;
    private Context aContext;
    //private Bus eventBus = Bus.getInstance();

    Bus clientEventBus;
    /**
     * This is the constructor. It sets a custom adapter as the source for all items displayed
     * in the grid. It also registers Facade to EventBus.
     * @param context This is the Context.
     */
    public SimGridFacade(Context context) {
        if(context != null) {
            gridAdapter = new GridAdapter(context);
        }

        aContext = context;
        clientEventBus = Bus.getInstance();
        clientEventBus.mEventBus.register(this);
    }

    /**
     * This method is what subscribes the Facade to the EventBus.
     * @param array This is the array that will populate the SimulationGrid.
     */
    @Subscribe
    public void onEvent(JSONArray array) {
        setUsingJSON(array);
    }





    /**
     * This method just sets the current adapter.
     * @param g This is the GridView.
     */
    public void setAdapter(GridView g) {
        gridView = g;
        gridView.setAdapter(gridAdapter);
        gridAdapter.addGrid(simulationGrid);
    }

    /**
     * This method fills the active SimulationGrid with information from a JSONArray coming from the server.
     * @param arr This is the JSON Array that is coming from the server.
     */
    public void setUsingJSON(JSONArray arr) {
        int indexId = 0;

        boolean primaryAlive = false;
        boolean controlledAlive = false;
        // String fullGrid = "";
        for(int i = 0; i < arr.length(); i++) {
            JSONArray jsonGrid = null;
            try {
                jsonGrid = arr.getJSONArray(i);
            }
            catch (Exception ex) {
                Log.d(TAG, "Bad list received from server");
            }

            if(jsonGrid != null) {

                for(int j = 0; j < jsonGrid.length(); j++) {
                    //for events
                    /*if(i == arr.length()-2) {
                        try {
                           // Log.e("usertank", "event id " + jsonGrid.getInt(j) + "\n");
                            if(jsonGrid.getInt(j) != 0) {
                               // Log.e("usertank", "event id " + jsonGrid.getInt(j) + "\n");
                            }
                            UserTank.getInstance(-1).didIDie(jsonGrid.getInt(j), j);
                        } catch (JSONException e) {
                            Log.d(TAG, "Bad value received from server");
                        }
                    }
                    else if(i == arr.length()-1) {
                        try {
                            if(jsonGrid.getInt(j) != 0) {
                                //Log.e("usertank", "event id " + jsonGrid.getInt(j) + "\n");
                            }
                            UserTank.getInstance(-1).didIDie(jsonGrid.getInt(j), j*10);
                        } catch (JSONException e) {
                            Log.d(TAG, "Bad value received from server");
                        }
                    }*/

                    if(i < arr.length()-2) {
                        int gridValue = 0;

                        try {
                            gridValue = jsonGrid.getInt(j);
                        } catch (JSONException e) {
                            Log.d(TAG, "Bad value received from server");
                        }

                        ImagePicker imagePicker = new ImagePicker();
                        Drawable newCell = imagePicker.pickImage(gridValue, aContext);

                        simulationGrid.setCell(indexId, newCell);
                        indexId++;
                    }
                }

            }
            if(gridView != null)
                gridView.invalidateViews();
        }
    }

    /**
    * Added for test
    * */
    public GridView getGridView() {return gridView;}
    public SimulationGrid getSimulationGrid(){
        return simulationGrid;
    }
    public SimGridFacade(){
    }



}
