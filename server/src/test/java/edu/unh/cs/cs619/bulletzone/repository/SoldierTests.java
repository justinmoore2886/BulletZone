package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import javax.swing.text.html.Option;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.terrain.Empty;

public class SoldierTests {
    private InMemoryGameRepository repo;

    @Test
    public void SoldierEjectReentry_SingleTankEmptyMap_RepoCreatesRemovesSoldierCorrectHealth()
            throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMap(1);

        Tank playerControlled = repo.join("", 1);

        // verify start as tank
        Assert.assertNotNull(playerControlled);
        Assert.assertTrue(playerControlled.getId() >= 0);
        Assert.assertEquals(1, playerControlled.getIdentifier());
        Assert.assertNotNull(playerControlled.getParent());

        // get tank parent Field Holder
        FieldHolder tankParent = playerControlled.getParent();

        // eject soldier
        Assert.assertTrue(repo.ejectSoldier(playerControlled.getId()));

        // verify now as soldier w/ starter health
        Assert.assertNotNull(playerControlled);
        Assert.assertEquals(2, playerControlled.getIdentifier());
        Assert.assertEquals(25, playerControlled.getLife());

        // verify empty tank in previous controlled tank cell
        Tank emptyTank = (Tank) tankParent.getEntity();
        Assert.assertEquals(3, emptyTank.getIdentifier());

        // reduce soldier health
        playerControlled.setLife(5);
        Assert.assertEquals(5, playerControlled.getLife());

        // get soldier parent Field Holder
        FieldHolder soldierParent = playerControlled.getParent();

        // rejoin soldier to emptyTank and verify
        Direction directionOfTank = Direction.Up;
        while (playerControlled.getParent().getNeighbor(directionOfTank) != tankParent) {
            directionOfTank = Direction.fromByte((byte) (Direction.toByte(directionOfTank) + 2));
        }

        playerControlled.setAllowedReEnterTime(0);
        playerControlled.setLastEnterTime(0);

        Assert.assertTrue(repo.move(playerControlled.getId(), directionOfTank));
        Assert.assertTrue(soldierParent.getEntity() instanceof Empty);
        Assert.assertEquals(1, playerControlled.getIdentifier());
    }

    @Test
    public void SoldierTurn_SingleSoldierEmptyMap_RepoReturnsTurnedSoldier() throws Exception{
        repo = new InMemoryGameRepository();
        repo.setMap(1);

        Tank tank = repo.join("", 2);

        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getParent());

        tank.setAllowedTurnInterval(0);

        // start facing up
        Assert.assertNotNull(tank.getDirection());
        Assert.assertSame(Direction.Up,tank.getDirection());

        // facing up, restricted turn down, facing up
        Assert.assertFalse(repo.turn(tank.getId(), Direction.Down));
        Assert.assertSame(Direction.Up,tank.getDirection());

        // turn right, facing right
        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertSame(Direction.Right,tank.getDirection());

        // facing right, restricted turn left, facing right
        Assert.assertFalse(repo.turn(tank.getId(), Direction.Left));
        Assert.assertSame(Direction.Right,tank.getDirection());

        // turn down, facing down
        Assert.assertTrue(repo.turn(tank.getId(), Direction.Down));
        Assert.assertSame(Direction.Down,tank.getDirection());

        // facing down, restricted turn up, facing down
        Assert.assertFalse(repo.turn(tank.getId(), Direction.Up));
        Assert.assertSame(Direction.Down,tank.getDirection());

        // turn left, facing left
        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertSame(Direction.Left,tank.getDirection());

        // facing left, restricted turn right, facing left
        Assert.assertFalse(repo.turn(tank.getId(), Direction.Right));
        Assert.assertSame(Direction.Left,tank.getDirection());
    }

    @Test
    public void SoldierMove_SingleSoldierEmptyMap_RepoReturnsMovedSoldier() throws Exception{
        repo = new InMemoryGameRepository();
        repo.setMap(1);

        Tank tank = repo.join("", 1);

        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getParent());

        tank.setAllowedMoveInterval(0);

        // start facing up
        Assert.assertNotNull(tank.getDirection());
        Assert.assertSame(Direction.Up,tank.getDirection());

        // get current and next cells
        FieldHolder parent = tank.getParent();
        FieldHolder neighbor = parent.getNeighbor(Direction.Up);

        // Facing up, restricted move left
        Assert.assertFalse(repo.move(tank.getId(), Direction.Left));
        Assert.assertSame(parent,tank.getParent());

        // Facing up, restricted move right
        Assert.assertFalse(repo.move(tank.getId(), Direction.Right));
        Assert.assertSame(parent,tank.getParent());

        tank.setLastMoveTime(0);

        // Facing up, allowed move forward
        Assert.assertTrue(repo.move(tank.getId(), Direction.Up));
        Assert.assertNotSame(parent,tank.getParent());
        Assert.assertSame(neighbor,tank.getParent());

        // get current and next cells
        parent = tank.getParent();
        neighbor = parent.getNeighbor(Direction.Down);

        tank.setLastMoveTime(0);

        // Facing up, allowed move backward
        Assert.assertTrue(repo.move(tank.getId(), Direction.Down));
        Assert.assertNotSame(parent,tank.getParent());
        Assert.assertSame(neighbor,tank.getParent());

        // get current and next cells
        parent = tank.getParent();
        neighbor = parent.getNeighbor(Direction.Down);

        tank.setLastMoveTime(0);

        // turn right, facing right
        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertSame(Direction.Right,tank.getDirection());

        // Facing right, restricted move up
        Assert.assertFalse(repo.move(tank.getId(), Direction.Up));
        Assert.assertSame(parent,tank.getParent());

        // Facing right, restricted move down
        Assert.assertFalse(repo.move(tank.getId(), Direction.Down));
        Assert.assertSame(parent,tank.getParent());

        // get current and next cells
        parent = tank.getParent();
        neighbor = parent.getNeighbor(Direction.Right);

        tank.setLastMoveTime(0);

        // Facing right, allowed move forward
        Assert.assertTrue(repo.move(tank.getId(), Direction.Right));
        Assert.assertNotSame(parent,tank.getParent());
        Assert.assertSame(neighbor,tank.getParent());

        // get current and next cells
        parent = tank.getParent();
        neighbor = parent.getNeighbor(Direction.Left);

        tank.setLastMoveTime(0);

        // Facing right, allowed move backward
        Assert.assertTrue(repo.move(tank.getId(), Direction.Left));
        Assert.assertNotSame(parent,tank.getParent());
        Assert.assertSame(neighbor,tank.getParent());

    }

    @Test
    public void testFire() {
        repo = new InMemoryGameRepository();
        repo.setMap(1);

        repo.setDebugJoinLocation(135);

        Tank tank = repo.join("", 1);

        // start facing up
        Assert.assertNotNull(tank.getDirection());
        Assert.assertSame(Direction.Up,tank.getDirection());

        tank.setLastFireTime(0);


    }
}
