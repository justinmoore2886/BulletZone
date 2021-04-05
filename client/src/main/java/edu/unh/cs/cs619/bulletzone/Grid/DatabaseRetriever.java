package edu.unh.cs.cs619.bulletzone.Grid;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Singletons.Bus;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

/**
 * <h1> DatabaseRetriever Class! </h1>
 * The database helper class is what is responsible for adding to the database in the poller and
 * retrieving from the database in the replay activity.
 *
 * @author Justin Moore
 * @version 1.0
 * @since 11/12/2018
 */
public class DatabaseRetriever extends SQLiteOpenHelper {

    private static final String TAG = "ClientSide";

    // Database stuff
    private static final String TABLE_NAME = "Games";
    private static final String COL1 = "rId";
    private static final String COL2 = "gameID";
    private static final String COL3 = "gridRow";
    private static final String COL4 = "userTankID";

    // Polling stuff
    private SimGridFacade facade;
    private Bus eventBus;
    private Activity activity;
    private ArrayList<String> lastGameGrids = new ArrayList<>();
    private UserTank userTank;

    // If we should polling the database, etc
    private boolean weAreInReplayMode = false;
    private boolean running = true;

    private static int indexInLastGameGrids = 0;
    private int pollingSpeed = 0;
    private Button replayButton;
    private RadioGroup radioGroup;

    public DatabaseRetriever(Context c) {
        super(c, TABLE_NAME, null, 1);
        userTank = UserTank.getInstance(-1);
    }

    /**
     * This is the constructor used when removing from the database.
     * @param f This is the SimGridFacade
     * @param c This is the Context
     * @param a This is the ReplayActivity
     * @param b This is the replayButton
     * @param r This is the collection of speed radio buttons
     */
    public DatabaseRetriever(SimGridFacade f, Context c, Activity a, Button b, RadioGroup r) {
        super(c, TABLE_NAME, null, 1);

        eventBus = Bus.getInstance();
        eventBus.mEventBus.register(this);
        facade  = f;
        activity = a;
        userTank = UserTank.getInstance(-1);

        replayButton = b;
        radioGroup = r;
    }

    /**
     * This runs when the database is created.
     * @param db The SQLiteDatabase we are creating
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL2 + " INTEGER," +
                COL3 + " TEXT," +
                COL4 + " TEXT" +
         ")";

        db.execSQL(createTable);
    }

    /**
     * This runs when the schema for the database is changed (e.g. added a column).
     * @param db The SQLiteDatabase we are updating
     * @param oldVersion Older version of database
     * @param newVersion Newer version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * This method is designated for EventBus posts. It receives the JSONArray that posts.
     */
    private class MessageEvent {
        private final JSONArray array;
        private MessageEvent(JSONArray a) {
            this.array = a;
        }
    }

    /**
     * This method is what subscribes the Poller to the EventBus.
     * @param event This is the event that occurred.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DatabaseRetriever.MessageEvent event) {
        facade.setUsingJSON(event.array);
    }

    /**
     * This method runs a thread that updates the grid view with the last games grids
     */
    private void runReplayThread() {
        new Thread() {
            public void run() {
                while(running) {
                    try {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (weAreInReplayMode)
                                    getGrids();
                            }
                        });
                        Thread.sleep(pollingSpeed);
                    }
                    catch (InterruptedException e) {
                        Log.d(TAG, "InterruptedException " + e);
                        running = false;
                    }
                }
            }
        }.start();
    }

    /**
     * This method gets grids from lastGameGrids to send to grid view
     */
    private void getGrids() {
        // There are still grids to displays
        if (indexInLastGameGrids != lastGameGrids.size()) {
            JSONArray array = null;
            try {
                JSONObject json = new JSONObject(lastGameGrids.get(indexInLastGameGrids));
                array = json.getJSONArray("grid");
            }
            catch (JSONException e) {
                Log.d(TAG, "JSONException " + e);
            }

            facade.setUsingJSON(array);
            indexInLastGameGrids++;
        }
        // We/ve displayed all the grids for a game
        else {
            replayButton.setText(R.string.playback);
            for(int i = 0; i < radioGroup.getChildCount(); i++)
                radioGroup.getChildAt(i).setEnabled(true);

            indexInLastGameGrids = 0;
            weAreInReplayMode = false;
            running = false;
            lastGameGrids.clear();
        }
    }

    /**
     * This method gets database data from the last played game
     */
    public boolean getDatabaseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        int currentGameId;

        // Get last game number
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        // Game has been played
        if (cursor.moveToFirst()) {
            cursor.moveToLast();

            currentGameId = cursor.getInt(1);

            if(userTank == null) {
                userTank = UserTank.getInstance(-1);
                Log.d(TAG, "getDatabaseData() userTank is null");
            }

            userTank.setTankId(Long.parseLong(cursor.getString(3)));
        }
        // No games played yet, empty database
        else {
            Log.d(TAG, "Error, no games played");
            return false;
        }

        // Get all game grids with that associated game number
        query = "SELECT * FROM Games where gameID = " + String.valueOf(currentGameId) + ";";
        cursor = db.rawQuery(query, null);

        try {
            String row;
            while (cursor.moveToNext()) {
                row = cursor.getString(2);
                lastGameGrids.add(row);
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.d(TAG, "Something went wrong in getDatabaseData(): " + e);
            return false;
        }

        cursor.close();
        return true;
    }

    /**
     * This method starts the polling for updating the replay grid view
     * @param s The speed at which we should playback at
     */
    public void startPolling(String s) {
        boolean dbGet = getDatabaseData();
        if(!dbGet)
            Log.d(TAG, "Error when calling getDatabaseData()");

        runReplayThread();

        pollingSpeed = getSpeed(s);
        weAreInReplayMode = true;
        running = true;
    }

    /**
     * This method resumes the replay
     */
    public void setPollAvailable(String s) {
        pollingSpeed = getSpeed(s);
        weAreInReplayMode = true;
    }

    /**
     * This method pauses the replay
     */
    public void setPollUnavailable() {
        weAreInReplayMode = false;
    }

    /**
     * This method gets the speed to replay the game back at
     * @param s This is the speed
     */
    private int getSpeed(String s) {
        switch (s) {
            case "Normal Speed":
                return 100;

            case "2x Speed":
                return 50;

            case "3x Speed":
                return 33;

            case "4x Speed":
                return 25;

            default:
                return 100;
        }
    }

    /**
     * This method checks if the database is empty
     */
    public boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        // Game has been played
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }
        else {
            cursor.close();
            return true;
        }
    }
}
