package edu.unh.cs.cs619.bulletzone.Grid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import androidx.test.core.app.ApplicationProvider;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class DatabaseTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void onCreate_CreateNewDatabase_ReturnTrue() {
        DatabaseAdder databaseAdder = new DatabaseAdder(context);
        SQLiteDatabase db = databaseAdder.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    @Test
    public void addData_InsertsIdAnGrid_ReturnTrue() {
        DatabaseAdder databaseAdder = new DatabaseAdder(context);
        long tankId = 2;
        String fullGrid = "{\"grid\":[[0,1000,1000,1000,0,1000,0,1000,1000,1000,0,0,0,0,0,0],[0,1000," +
                "0,0,0,1000,10000300,1000,0,1000,0,0,0,0,0,0],[0,1500,1000,1000,0,1000,0,1000,1000,1000,0," +
                "0,0,0,0,0],[0,1500,0,1000,0,1000,0,0,0,1000,0,0,0,0,0,0],[0,1500,1500,1500,0,1500,0,1500," +
                "1500,1000,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]," +
                "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0," +
                "0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0," +
                "0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0," +
                "0,0,0,0,0,0,0,0,0,0,0,0,0]],\"timeStamp\":1542064768645}";

        assertTrue(databaseAdder.addData(fullGrid, tankId));
    }

    @Test
    public void getDatabaseData_RetrieveThreeRows_ReturnTrue() {
        DatabaseAdder databaseAdder = new DatabaseAdder(context);
        UserTank userTank = UserTank.getInstance(13);

        long tankId = 2;
        String fullGrid = "{\"grid\":[[0,1000,1000,1000,0,1000,0,1000,1000,1000,0,0,0,0,0,0],[0,1000," +
                "0,0,0,1000,10000300,1000,0,1000,0,0,0,0,0,0],[0,1500,1000,1000,0,1000,0,1000,1000,1000,0," +
                "0,0,0,0,0],[0,1500,0,1000,0,1000,0,0,0,1000,0,0,0,0,0,0],[0,1500,1500,1500,0,1500,0,1500," +
                "1500,1000,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]," +
                "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0," +
                "0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0," +
                "0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0," +
                "0,0,0,0,0,0,0,0,0,0,0,0,0]],\"timeStamp\":1642064768645}";
        databaseAdder.addData(fullGrid, tankId);

        tankId = 2;
        fullGrid = "{\"grid\":[[0,1000,1000,1000,0,1000,0,1000,1000,1000,0,0,0,0,0,0],[0,1000," +
                "0,0,0,1000,10000300,1000,0,1000,0,0,0,0,0,0],[0,1500,1000,1000,0,1000,0,1000,1000,1000,0," +
                "0,0,0,0,0],[0,1500,0,0,0,1000,0,0,0,1000,0,0,0,0,0,0],[0,1500,1500,1500,0,1500,0,1500," +
                "1500,1000,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]," +
                "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0," +
                "0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,100,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0," +
                "0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0," +
                "0,0,0,0,0,0,0,0,0,0,0,0,0]],\"timeStamp\":1572064768645}";
        databaseAdder.addData(fullGrid, tankId);

        tankId = 2;
        fullGrid = "{\"grid\":[[0,1000,1000,1000,0,1000,0,1000,1000,1000,0,0,0,0,0,0],[0,1000," +
                "0,0,0,1000,10000300,1000,0,1000,0,0,0,0,0,0],[0,1500,1000,1000,0,1000,0,1000,1000,1000,0," +
                "0,0,0,0,0],[0,1500,0,1000,0,0,0,0,0,1000,0,0,0,0,0,0],[0,1500,1500,1500,0,1500,0,1500," +
                "1500,1000,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]," +
                "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,1000,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0," +
                "0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0," +
                "0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0," +
                "0,0,0,0,0,0,0,0,0,0,0,0,0]],\"timeStamp\":1542064768645}";
        databaseAdder.addData(fullGrid, tankId);

        DatabaseRetriever databaseRetriever = new DatabaseRetriever(context);
        assertTrue(databaseRetriever.getDatabaseData());
    }

    @Test
    public void isDatabaseEmpty_DatabaseEmpty_ReturnTrue() {
        DatabaseRetriever databaseRetriever = new DatabaseRetriever(context);
        assertTrue(databaseRetriever.isDatabaseEmpty());
    }
}