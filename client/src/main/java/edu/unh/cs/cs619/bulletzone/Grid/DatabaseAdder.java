package edu.unh.cs.cs619.bulletzone.Grid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdder extends SQLiteOpenHelper {

    private static final String TAG = "ClientSide";

    // Database stuff
    private static final String TABLE_NAME = "Games";
    private static final String COL1 = "rId";
    private static final String COL2 = "gameID";
    private static final String COL3 = "gridRow";
    private static final String COL4 = "userTankID";

    // For keeping track of the current game
    private static int gameID = 0;
    private static boolean didWeGetLastID = false;


    /**
     * This is the constructor used when adding to database.
     * @param c This is the Context
     */
    public DatabaseAdder(Context c) {
        super(c, TABLE_NAME, null, 1);
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
     * This method adds to the database from Poller.java
     * @param fullGrid This is a string version of the JSON object received
     * @param userTankId This is the users tank ID
     */
    public boolean addData(String fullGrid, long userTankId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /* Get the last row in the database (last played game) */
        if(!didWeGetLastID) {

            String queryToGetLastGameID = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(queryToGetLastGameID, null);

            // Get the game ID of that game
            if (cursor.moveToFirst()) {
                cursor.moveToLast();

                gameID = cursor.getInt(1) + 1;
            }
            cursor.close();

            didWeGetLastID = true;
        }

        contentValues.put(COL2, gameID);
        contentValues.put(COL3, fullGrid);
        contentValues.put(COL4, String.valueOf(userTankId));

        long result = 0;

        try {
            result  = db.insert(TABLE_NAME, null, contentValues);
        }
        catch (Exception e){
            Log.d(TAG, "Error: " + e);
        }

        // If data as inserted incorrectly it will return -1
        return result != -1;
    }

    /**
     * This method sets didWeGetLastID to false, which is used to track if we have gotten the last
     * played game ID or not. If not, we know to update GAME ID in database in add Data
     */
    public void setDidWeGetLastIDToFalse() { didWeGetLastID = false; }
}
