package edu.unh.cs.cs619.bulletzone;

import org.junit.Before;
import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.web.PowerupTests;
import edu.unh.cs.cs619.bulletzone.web.TimedTests;

public class _AllServerTests {
    TimedTests serverTimedTests = new TimedTests();
    PowerupTests powerUpTests = new PowerupTests();

    @Before
    public void setup(){
        serverTimedTests.setup();
    }

    @Test
    public void Milestone_1_TimedTests() throws Exception{
        serverTimedTests.t00_JoinDisconnect_MultipleClients_TanksCreateAndRemoveCorrectIDs();
        serverTimedTests.t01_Turn_TimedTank_TurnsCorrectDirectionDelays();
        serverTimedTests.t02_Move_TimedTankOpenTerrain_MovesCorrectDelays();
        serverTimedTests.t03_Fire_TimedTank_FiresCorrectDelaysLimits();
    }

    @Test
    public void Milestone_2_TimedSoldierTests() throws Exception{
        serverTimedTests.t06_EjectReenter_TimedMultipleClients_CorrectSoldiersAddRemoveCorrectHealthsDelays();
        serverTimedTests.t07_Turn_TimedSoldier_TurnsCorrectDelays();
        serverTimedTests.t08_Move_TimedSoldierOpenTerrain_MovesCorrectDelays();
        serverTimedTests.t11_Fire_TimedSoldier_FiresCorrectDelaysLimits();
    }

    @Test
    public void Milestone_2_TimedTerrainTests() throws Exception{
        serverTimedTests.t04_Move_TimedTankHillTerrain_MovesCorrectDelays();
        serverTimedTests.t05_Move_TimedTankDebrisTerrain_MovesCorrectDelays();

        serverTimedTests.t09_Move_TimedSoldierHillTerrain_MovesCorrectDelays();
        serverTimedTests.t10_Move_TimedSoldierDebrisTerrain_MovesCorrectDelays();
    }

    @Test
    public void Milestone_2_TimedPowerUpTests() throws Exception{
        serverTimedTests.t12_Move_TimedTankAntiGravMultipleTerrains_MovesCorrectDelays();
        serverTimedTests.t13_Fire_TimedTankAntiGrav_FiresCorrectLimits();

        serverTimedTests.t14_Move_TimedSoldierAntiGravMultipleTerrains_MovesCorrectDelays();
        serverTimedTests.t15_Fire_TimedSoldierAntiGrav_FiresCorrectLimits();

        serverTimedTests.t16_Move_TimedTankFusionReactMultipleTerrains_MovesCorrectDelays();
        serverTimedTests.t17_Fire_TimedTankFusionReact_FiresCorrectLimits();

        serverTimedTests.t18_Move_TimedSoldierFusionReactMultipleTerrains_MovesCorrectDelays();
        serverTimedTests.t19_Fire_TimedSoldierFusionReact_FiresCorrectLimits();
    }

    @Test
    public void Milestone_2_UntimedPowerUpTests() throws Exception{
        powerUpTests.setup();
        powerUpTests.moveCheck_TankAntiGravityNoTerrain_FasterTankSlowedFire();
        powerUpTests.moveCheck_TankFusionReactorNoTerrain_SlowedTankWithFourBullets();
    }

    @Test
    public void MileStone_3_TimedShipTests() throws Exception{
        serverTimedTests.t20_Turn_TimedShip_TurnsCorrectDirectionDelays();
        serverTimedTests.t21_Move_TimedShipOpenWater_MovesCorrectDelays();
        serverTimedTests.t22_Fire_TimedShip_FiresCorrectDelaysLimits();
    }

    @Test
    public void Milestone_3_TimedSoldierTests() throws Exception{
        serverTimedTests.t23_Move_TimedSoldierOpenWaterAndCoast_TurnsMovesCorrectDelays();
        serverTimedTests.t24_Fire_TimedSoldierOpenWaterAndCoast_FiresCorrectLimits();
        serverTimedTests.t25_Move_TimedSoldierOpenWaterAndCoast_TurnsMovesCorrectDelays();
        serverTimedTests.t26_Fire_TimedSoldierPowerPackOpenWaterAndCoast_FiresCorrectLimits();
    }


}
