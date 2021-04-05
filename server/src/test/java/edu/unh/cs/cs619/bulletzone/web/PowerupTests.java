package edu.unh.cs.cs619.bulletzone.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import edu.unh.cs.cs619.bulletzone.model.powerups.AntiGravity;
import edu.unh.cs.cs619.bulletzone.model.powerups.FusionReactor;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerRack;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerUp;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public class PowerupTests {

    private InMemoryGameRepository repo = new InMemoryGameRepository();

    @Before
    public void setup(){
        repo.setMap(1);
    }

    @Test
    public void moveCheck_TankFusionReactorNoTerrain_SlowedTankWithFourBullets(){
        Tank tank = repo.join("", 1);
        tank.setPowerUp(new FusionReactor(0));
        Assert.assertEquals(625,tank.getAllowedMoveInterval());
        Assert.assertEquals(250,tank.getAllowedFireInterval());
    }

    @Test
    public void moveCheck_TankAntiGravityNoTerrain_FasterTankSlowedFire(){
        Tank tank = repo.join("", 1);
        PowerUp testPowerUp = new AntiGravity(0);
        testPowerUp.applyEffect(tank);
        Assert.assertEquals(250,tank.getAllowedMoveInterval());
        Assert.assertEquals(600,tank.getAllowedFireInterval());
    }
}
