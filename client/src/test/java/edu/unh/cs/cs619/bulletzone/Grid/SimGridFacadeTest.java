package edu.unh.cs.cs619.bulletzone.Grid;

import android.content.Context;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


import androidx.test.core.app.ApplicationProvider;
import edu.unh.cs.cs619.bulletzone.Grid.SimGridFacade;
import edu.unh.cs.cs619.bulletzone.Singletons.Bus;

@RunWith(RobolectricTestRunner.class)
public class SimGridFacadeTest {
    Bus EB = new Bus();
    private SimGridFacade myFacade;

    private Context mContext = ApplicationProvider.getApplicationContext();
    private GridView mGridView = new GridView(mContext);


    @Test
    public void t00_setAdapter_AddGridViewAndContext_ReturnsGridView() {
        myFacade = new SimGridFacade(mContext); // create new facade
        myFacade.setAdapter(mGridView);
        assert(mGridView == myFacade.getGridView());
    }

    @Test
    public void t01_setUsingJSON_SampleJSONbject_setsSimulationGrid() throws Exception{
        myFacade = new SimGridFacade(mContext);

        int[][] fakeJSON = new int[][] {
                {0,1000,1000,1000,0,1000,0,1000,1000,1000,0,0,0,0,0,0},
                {0,1000,0,0,0,1000,0,1000,0,1000,0,0,0,0,0,0},
                {0,1000,1000,1000,0,1000,0,1000,1000,1000,0,0,0,0,0,0},
                {0,1000,0,1000,0,1000,0,0,0,1000,0,0,0,0,0,0},
                {0,1000,1000,1000,0,1000,0,1000,1000,1000,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,2003001,0,0,0,0,0,0,0,0,2003001,2003001,0,2003001,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,10050020,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };

        JSONArray testSubGrid = new JSONArray();
        JSONArray testGrid = new JSONArray();

        for(int i=0;i<16;i++){
            testSubGrid = new JSONArray();
            for(int j=0;j<16;j++){
                testSubGrid.put(fakeJSON[i][j]);
            }
            testGrid.put(testSubGrid);
        }

        myFacade.setUsingJSON(testGrid);

        SimulationGrid storedGrid = myFacade.getSimulationGrid();

        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                Assert.assertEquals(fakeJSON[i][j],storedGrid.getCell((i*16)+j));
            }
        }
    }
}
