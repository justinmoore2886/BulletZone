package edu.unh.cs.cs619.bulletzone.Grid;

import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.Grid.SimulationGrid;

public class SimulationGridTest {
    @Test
    public void setAndGetCell1_AddCell_CellValueIs3() {
        SimulationGrid mSimulationGrid = new SimulationGrid();
        mSimulationGrid.setCell(5, 3);
        assert(mSimulationGrid.getCell(5) == 3);
    }
    @Test
    public void setAndGetCell2_AddCell_CellValueIs10() {
        SimulationGrid mSimulationGrid = new SimulationGrid();
        mSimulationGrid.setCell(17, 10);
        assert(mSimulationGrid.getCell(17) == 10);
    }

}