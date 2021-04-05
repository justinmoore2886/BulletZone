package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;

public interface GameRepository {

    Tank join(String ip, int identifier);

    Tank whoAmI(String ip)
            throws TankDoesNotExistException;

    int whereAmI(String ip)
            throws TankDoesNotExistException;

    int howManyBullets(String ip)
            throws TankDoesNotExistException;

    byte whichWay(String ip)
            throws TankDoesNotExistException;

    int[][] getGrid();

    boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean fire(long tankId, Direction directionFire)
            throws TankDoesNotExistException, LimitExceededException;

    boolean eject(long tankId)
            throws TankDoesNotExistException;

    boolean ejectSoldier(long tankId)
            throws TankDoesNotExistException;

    void leave(long tankId)
            throws TankDoesNotExistException;

    long amIAlive(long primaryId, long remoteId)throws TankDoesNotExistException;
}
