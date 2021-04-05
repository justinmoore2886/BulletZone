package edu.unh.cs.cs619.bulletzone.Grid;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import edu.unh.cs.cs619.bulletzone.R;

/**
 * <h1> GridAdapter Class! </h1>
 * The GridAdapter class implements methods that use the grid, such as grabbing specific cells from
 * the grid or creating the grid in general.
 *
 * @author Justin Moore
 * @version 1.0
 * @since 10/31/2018
 */
public class GridAdapter extends BaseAdapter implements ListAdapter {

    private Context context;              // Context of current state of the object
    public SimulationGrid simulationGrid;

    /**
     * This is the constructor. It sets context.
     * @param c This is the context.
     */
    public GridAdapter(Context c) {
        context = c;
    }

    /**
     * This method gets the cell count in the grid.
     * @return 256 The grid is always 16x16 which is 256.
     */
    public int getCount() {
        return 256;
    }

    /**
     * This method gets the picture in a specific cell position in the grid.
     * @param position This is the position of the cell within the grid.
     * @return simulationGrid.getCell(position) This returns a picture id from the grid.
     */
    public Object getItem(int position) {
        return (Drawable) simulationGrid.getCell(position);
    }

    /**
     * This method gets the position of a picture in the grid.
     * @param position This is the position in the grid
     * @return position This is also the position in the grid.
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * This method adds a grid to this class for GridCell retrieval.
     * @param s This is the existing simulationgrid.
     */
    public void addGrid(SimulationGrid s) {
        simulationGrid = s;
    }

    /**
     * This method creates a new imageview for each item referenced by the adapter.
     * @param position This is the position of the GridCell
     * @param convertView Make a new view
     * @param parent Parent of view
     * @return imageView This is the existing or new ImageView.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        // If it's not recycled, initialize some attributes
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(55, 55)); // Height/width
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);             // Crop type
        }
        else {
            imageView = (ImageView) convertView;
        }

        if(simulationGrid == null) {
            imageView.setImageResource(R.drawable.blank);
        }

        else {
            imageView.setImageDrawable(simulationGrid.getCell(position));
        }

        return imageView;
    }
}