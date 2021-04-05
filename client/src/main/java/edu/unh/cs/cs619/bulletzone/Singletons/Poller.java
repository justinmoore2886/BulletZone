package edu.unh.cs.cs619.bulletzone.Singletons;

import android.content.Context;
import android.util.Log;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.concurrent.ScheduledExecutorService;

import edu.unh.cs.cs619.bulletzone.Grid.DatabaseAdder;
import edu.unh.cs.cs619.bulletzone.Grid.SimGridFacade;

/**
 * <h1> Poller Class! </h1>
 * The poller class is what is responsible for sending a query to the server for a new grid. It gets
 * a new grid and posts it on the EventBus.
 *
 * @author Justin Moore
 * @version 1.0
 * @since 10/31/2018
 */
public class Poller {

    private static final String TAG = "ClientSide";
    private RequestQueue queue;
    private SimGridFacade facade;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private Bus eventBus = Bus.getInstance();
    private String url;
    private static Poller pollerInstance = null;
    private DatabaseAdder databaseAdder;
    private UserTank userTank;
    private static JSONArray array;
    long alive;
    private boolean haveWeStoppedPoller = false;
    private boolean canWeAddToDb = true;

    /**
     * This is the constructor. It creates a scheduled thread pool with 5 core threads
     * @param f This is the SimGridFacade
     * @param c This is the Context
     * @param u This is the URL
     */
    protected Poller(SimGridFacade f, Context c, String u) {
        facade  = f;
        userTank = UserTank.getInstance(-1);
        queue = Volley.newRequestQueue(c);
        eventBus.mEventBus.register(this);
        url = u;
        databaseAdder = new DatabaseAdder(c);
    }

    /**
     * This method provided controlled access to a sole instance of Poller.
     * @param f This is the SimGridFacade
     * @param c This is the Context
     * @return pollerInstance This is the single instance of poller.
     */
    public static Poller getInstance(SimGridFacade f, Context c, String u) {
        if (pollerInstance == null)
            pollerInstance = new Poller(f, c, u);

        return pollerInstance;
    }

    public JSONArray getArray() {
        return array;
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
     * This method defines the action for what occurs when a GET occurs. It also allows the thread
     * to be used every 500 MS.
     * @exception Exception This throws if something occurs with the threads sleeping.
     */
    private Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            try {
                jsonParse();
                //amIAlive();
                Thread.sleep(100);
            }
            catch (Exception ex) {
                Log.d(TAG, "Runnable periodicTask exited");
            }
        }
    };

    private ScheduledFuture<?> periodicFuture = executor.scheduleAtFixedRate(periodicTask, 100, 100, TimeUnit.MILLISECONDS);

    /**
     * This method is what subscribes the Poller to the EventBus.
     * @param event This is the event that occurred.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        facade.setUsingJSON(event.array);
    }

    /**
     * This method throws the JSONArray onto the EventBus.
     * @exception JSONException It throws if there is a problem with parsing the JSON.
     * @exception Exception It throws if there is an error in Runnable periodicTask.
     */
    private void jsonParse() {
        try {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String fullGrid = response.toString();
                                boolean insertData = true;

                                if(canWeAddToDb)
                                    insertData = databaseAdder.addData(fullGrid, userTank.getTankId());

                                if(!insertData)
                                    Log.d(TAG, "Problem with inserting into database");

                                array = response.getJSONArray("grid");
                                eventBus.mEventBus.post(array);
                            }
                            catch (JSONException e) {
                                Log.d(TAG, "Problem with JSON API when parsing from URL");
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Volley error. Check internet connection or URL validity");
                    error.printStackTrace();
                }
            });
            queue.add(request);
        }
        catch(Exception e) {
            Log.d(TAG, "Error in Runnable periodicTask");
        }
    }
    /**
     * This method stops the poller when the user exits ClientActivity.java
     */
    public void stopPoller() {
        if(!haveWeStoppedPoller) {
            canWeAddToDb = false;
            haveWeStoppedPoller = true;

            periodicFuture.cancel(true);
            pollerInstance = null;

            databaseAdder.setDidWeGetLastIDToFalse();
            userTank.setInstanceToNull();
        }
    }
}
