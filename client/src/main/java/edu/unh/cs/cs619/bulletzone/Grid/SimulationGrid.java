/*

This is basically a collection that holds GridCells in array form for quicker access.

*/

package edu.unh.cs.cs619.bulletzone.Grid;

import android.graphics.drawable.Drawable;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * <h1> SimulationGrid Class! </h1>
 * The SimulationGrid class implements a simple get and set for putting and recieving GridCells
 * to/from the grid.
 *
 * @author toBeDecided
 * @version 1.0
 * @since 10/31/2018
 */
public class SimulationGrid {

    private Drawable gridCollection[];

    /**
     * This is the constructor. It sets the grid as an array of 256 ints initialized to 0.
     */
    public SimulationGrid() {
        gridCollection = new Drawable[256];
    }

    /**
     * This method grabs a GridCell from the grid, given a position.
     * @param index This is the position.
     * @return gridCollection[index] This returns a GridCell.
     */
    public Drawable getCell(int index) {
        return gridCollection[index];
    }

    /**
     * This method sets a GridCell into the grid, given a cell and position.
     * @param index This is the position.
     * @param cell This is the given GridCell.
     */
    public void setCell(int index, Drawable cell) {
        gridCollection[index] = cell;
    }
}
