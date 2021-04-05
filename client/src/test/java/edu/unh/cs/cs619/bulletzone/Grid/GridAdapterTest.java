package edu.unh.cs.cs619.bulletzone.Grid;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import edu.unh.cs.cs619.bulletzone.Grid.GridAdapter;
import edu.unh.cs.cs619.bulletzone.Grid.SimulationGrid;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class GridAdapterTest{

    private final Context context = RuntimeEnvironment.application.getApplicationContext();
    private final SimulationGrid initialGrid = new SimulationGrid();
    private final GridAdapter gridAdapter = new GridAdapter(context);

    @Before
    public void setUp() throws Exception {
        for(int i=0;i<256;i++) {
            initialGrid.setCell(i,10);
        }
    }

    @Test
    public void getCount_FullGridSize_Returns256() {
        assertEquals(256, gridAdapter.getCount());
    }

    @Test
    public void getItemId_FullGridOfCellsWithId_Return13() {
        Long result = gridAdapter.getItemId(13);
        assert(result ==  13);
    }

    @Test
    public void addGrid_FullSimulationGridAdded_EnsureAdded() {
        gridAdapter.addGrid(initialGrid);
        assert(initialGrid == gridAdapter.simulationGrid);
    }
}
