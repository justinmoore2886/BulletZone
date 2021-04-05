package edu.unh.cs.cs619.bulletzone.model;

/**
 * <h1> ShipInfo Class! </h1>
 * The ShipInfo class provides the info for a ship.
 *
 * @author Bing
 * @version 1.0
 * @since 12/06/2018
 */
public class ShipInfo {
    public static final String TAG = "Ship";
    public static int allowedMoveInterval = 750;
    public static int allowedTurnInterval = 250;
    public static int allowedMainFireInterval = 300;
    public static int allowedSideFireInterval = 250;
    public static int allowedNumberOfBullets = 8;
    public static int life = 100;
    public static int allowedReEnterTime = 3000;
}
