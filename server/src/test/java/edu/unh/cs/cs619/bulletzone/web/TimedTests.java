package edu.unh.cs.cs619.bulletzone.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Random;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.FireBullet;
import edu.unh.cs.cs619.bulletzone.model.MovementCheck;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)   // wanted ordered, for server status
public class TimedTests {

    private static final int FIELD_DIM = 16;
    private static final int DEFAULT_JOIN_METHOD = 1;
    private static final int CONTROLLED_JOIN_METHOD = 0;

    private class MockMoveCheck extends MovementCheck{
        @Override
        public long timecheck(Tank tank, String use){
            long millis = _SystemTime;
            long lastactiontime;
            if(use == "move")
                lastactiontime = tank.getLastMoveTime();
            else if(use == "bullet")
                lastactiontime = tank.getLastFireTime();
            else
                lastactiontime = tank.getLastTurnTime();

            if(millis < lastactiontime)
                return 0;
            else
                return millis;
        }
    }

    private class MockFireBullet extends FireBullet{

    }

    private MockMvc mockMvc;
    private MovementCheck _testMC;
    private FireBullet _testFB;
    private InMemoryGameRepository repository;
    private GamesController gamesController;
    private InMemoryGameRepository repositorySoldier;
    private GamesController gamesControllerSoldier;

    // Server mock

    @Before
    public void setup(){
        this._testMC = new MockMoveCheck();
        this._testFB = new MockFireBullet();
        this.repository = new InMemoryGameRepository(1,_testMC,_testFB,1);
        this.gamesController = new GamesController(repository);

        this.mockMvc = MockMvcBuilders.standaloneSetup(gamesController).build();
    }

    @Test
    public void t00_JoinDisconnect_MultipleClients_TanksCreateAndRemoveCorrectIDs()
            throws Exception {

        repository.setMap(1);
        repository.disablePowerups();
        repository.disableDebugJoin();
        repository.setPlayerJoinType(1);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub [] clients = new ClientStub[2];
        clients[0] = new ClientStub(mockMvc,1);
        clients[1] = new ClientStub(mockMvc,1);

        // Snapshot_INIT of playfield with First Client
        assertNotNull("getGrid failed to return\n",
                gameState.initialState = gameState.getGrid());

        // First client joins
        assertEquals(201,clients[0].joinGame());
        assertEquals(clients[0].tankID,clients[0].whoAmI());
        clients[0].location = clients[0].whereAmI();

        // Snapshot_01 of playfield with First Client
        gameState.states.add(gameState.getGrid());

        // Second client joins
        assertEquals(201,clients[1].joinGame());
        assertEquals(clients[1].tankID,clients[1].whoAmI());
        clients[1].location = clients[1].whereAmI();

        // Snapshot_02 of playfield with Second Client
        gameState.states.add(gameState.getGrid());

        // Locations and IDs separate and distinct
        assertNotEquals(clients[0].tankID,clients[1].tankID);
        assertNotEquals(clients[0].location,clients[1].location);

        // First client leaves
        clients[0].leaveGame();

        // Snapshot_03 of playfield First Client Gone, Second Client Present
        gameState.states.add(gameState.getGrid());

        // Check that first client left, second client present and unchanged
        assertEquals(-1,clients[0].whoAmI());
        assertEquals(clients[1].tankID,clients[1].whoAmI());
        assertEquals(clients[1].location,clients[1].whereAmI());

        clients[1].leaveGame();

        // Snapshot_04 of playfield Both Clients gone
        gameState.states.add(gameState.getGrid());
    }

    @Test
    public void t01_Turn_TimedTank_TurnsCorrectDirectionDelays()
            throws Exception {

        _testMC._SystemTime = 0;
        repository.setMap(1);
        repository.disableDebugJoin();
        repository.disablePowerups();

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        // Clockwise, following time delay restrictions
        for(int i=0;i<4;i++) {
            // ELAPSED_TIME -> 500ms -> 1000ms -> ...
            _testMC._SystemTime += 525;

            byte prevDirection = client.direction;

            client.turn((byte) ((prevDirection + 2) % 8));
            client.direction = client.whichWay();

            assertEquals((prevDirection + 2) % 8, client.direction);
        }

        // AntiClockwise, following time delay restrictions
        for(int i=0;i<4;i++){
            // ELAPSED_TIME
            _testMC._SystemTime += 525;

            byte prevDirection = client.direction;

            client.turn((byte)(Math.abs(prevDirection - 2)%8));
            client.direction = client.whichWay();

            assertEquals(Math.abs(prevDirection - 2) % 8,client.direction);
        }

        // Clockwise, attempting to move before delay
        byte curDirection = client.direction;
        byte nextDirection = (byte)((curDirection + 2) % 8);

        // Turn and expect no change
        _testMC._SystemTime += 475;
        client.turn(nextDirection);
        assertNotEquals(curDirection,nextDirection);
        assertEquals(client.direction,client.whichWay());
    }

    @Test
    public void t02_Move_TimedTankOpenTerrain_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(1);
        repository.disablePowerups();
        repository.disableDebugJoin();

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();

        // Move right following time delay restrictions, w/wraparound
        for(int i=0;i<32;i++){
            _testMC._SystemTime += 525;

            long prevLocation = client.location;
            long predictNext = prevLocation + 1;

            if((predictNext % FIELD_DIM) == 0)
                predictNext = prevLocation - (prevLocation % FIELD_DIM);

            assertTrue(client.move(Direction.toByte(Direction.Right)));
            client.location = client.whereAmI();

            assertEquals(predictNext,client.location);
        }

        // Turn client to face down
        _testMC._SystemTime += 525;
        while(Direction.fromByte(client.direction) != Direction.Down) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        // Move vertically following time delay restrictions, w/wraparound
        for(int i=0;i<32;i++){
            _testMC._SystemTime += 525;

            long prevLocation = client.location;
            long predictNext = prevLocation + FIELD_DIM;

            if(predictNext >= (FIELD_DIM*FIELD_DIM))
                predictNext = predictNext % FIELD_DIM;

            client.move(Direction.toByte(Direction.Down));
            client.location = client.whereAmI();

            assertEquals(predictNext,client.location);
        }

        long curLocation = client.whereAmI();

        // Move and expect no change
        _testMC._SystemTime += 475;
        client.move(Direction.toByte(Direction.Right));
        assertEquals(curLocation,client.whereAmI());
        client.move(Direction.toByte(Direction.Up));
        assertEquals(curLocation,client.whereAmI());

        client.leaveGame();
    }

    @Test
    public void t03_Fire_TimedTank_FiresCorrectDelaysLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(6);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(119);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        int overDelay = client.allowedFireInterval + 25;
        int underDelay = client.allowedFireInterval -25;

        // Client joins
        assertEquals(201,client.joinGame());

        // Turn client to face up
        _testMC._SystemTime += overDelay;
        while(Direction.fromByte(client.direction) != Direction.Up) {
            _testMC._SystemTime += overDelay;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        // Client fires following limits and delays
        _testMC._SystemTime += overDelay;

        // fires
        assertTrue(client.fire(1));

        // fails to fire - time delay
        assertFalse(client.fire(1));

        // fails to fire - bullet limit
        assertFalse(client.fire(1));

        client.leaveGame();
    }

    @Test
    public void t04_Move_TimedTankHillTerrain_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(2);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        int overDelay = client.allowedMoveInterval + 525;
        int underDelay = client.allowedMoveInterval + 475;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Hill 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += underDelay;

        assertFalse(client.move(Direction.toByte(Direction.Right)));     // Leave Hill Delay
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Open 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Hill 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += underDelay;

        assertFalse(client.move(Direction.toByte(Direction.Right)));    // Leave Hill Delay
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));    // Enter Open 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Hill 103->104
        assertEquals(104,client.whereAmI());
    }

    @Test
    public void t05_Move_TimedTankDebrisTerrain_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(3);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Debris 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Open 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Debris 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Open 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));    // Enter Debris 103->104
        assertEquals(104,client.whereAmI());
    }

    @Test
    public void t06_EjectReenter_TimedMultipleClients_CorrectSoldiersAddRemoveCorrectHealthsDelays()
            throws Exception {
        repository.setMap(1);
        repository.disablePowerups();
        repository.disableDebugJoin();
        repository.setPlayerJoinType(1);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub [] clients = new ClientStub[2];
        clients[0] = new ClientStub(mockMvc,1);
        clients[1] = new ClientStub(mockMvc,1);

        // Snapshot_INIT of playfield with First Client
        assertNotNull("getGrid failed to return\n",
                gameState.initialState = gameState.getGrid());

        // First client joins
        assertEquals(201,clients[0].joinGame());
        assertEquals(clients[0].tankID,clients[0].whoAmI());
        clients[0].location = clients[0].whereAmI();

        // Snapshot_01 of playfield with First Client
        gameState.states.add(gameState.getGrid());

        // Second client joins
        assertEquals(201,clients[1].joinGame());
        assertEquals(clients[1].tankID,clients[1].whoAmI());
        clients[1].location = clients[1].whereAmI();

        // Snapshot_02 of playfield with Second Client
        gameState.states.add(gameState.getGrid());

        // Locations and IDs separate and distinct
        assertNotEquals(clients[0].tankID,clients[1].tankID);
        assertNotEquals(clients[0].location,clients[1].location);

        assertEquals(202,clients[0].ejectSoldier());
        gameState.states.add(gameState.getGrid());
        assertEquals(2,
                gameState.countGridDiff(
                        gameState.states.get((gameState.states.size())-1),
                        gameState.states.get((gameState.states.size())-2)));

        // First client leaves
        clients[0].leaveGame();

        // Snapshot_03 of playfield First Client Gone, Second Client Present
        gameState.states.add(gameState.getGrid());

        // Check that first client left, second client present and unchanged
        assertEquals(-1,clients[0].whoAmI());
        assertEquals(clients[1].tankID,clients[1].whoAmI());
        assertEquals(clients[1].location,clients[1].whereAmI());

        clients[1].leaveGame();

        // Snapshot_04 of playfield Both Clients gone
        gameState.states.add(gameState.getGrid());
    }

    @Test
    public void t07_Turn_TimedSoldier_TurnsCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(1);
        repository.disableDebugJoin();
        repository.disablePowerups();
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        // Clockwise, following time delay restrictions
        for(int i=0;i<4;i++) {
            // ELAPSED_TIME -> 500ms -> 1000ms -> ...
            _testMC._SystemTime += 0;

            byte prevDirection = client.direction;

            client.turn((byte) ((prevDirection + 2) % 8));
            client.direction = client.whichWay();

            assertEquals((prevDirection + 2) % 8, client.direction);
        }

        // AntiClockwise, following time delay restrictions
        for(int i=0;i<4;i++){
            // ELAPSED_TIME
            _testMC._SystemTime += 0;

            byte prevDirection = client.direction;

            client.turn((byte)(Math.abs(prevDirection - 2)%8));
            client.direction = client.whichWay();

            assertEquals(Math.abs(prevDirection - 2) % 8,client.direction);
        }

        // Clockwise, attempting to move before delay
        byte curDirection = client.direction;
        byte nextDirection = (byte)((curDirection + 2) % 8);

        // Turn and expect no change
        _testMC._SystemTime += 0;
        client.turn(nextDirection);
        assertNotEquals(curDirection,nextDirection);
        assertEquals(nextDirection,client.whichWay());
    }

    @Test
    public void t08_Move_TimedSoldierOpenTerrain_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(1);
        repository.disablePowerups();
        repository.disableDebugJoin();
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 1025;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();

        // Move right following time delay restrictions, w/wraparound
        for(int i=0;i<32;i++){
            _testMC._SystemTime += 1025;

            long prevLocation = client.location;
            long predictNext = prevLocation + 1;

            if((predictNext % FIELD_DIM) == 0)
                predictNext = prevLocation - (prevLocation % FIELD_DIM);

            assertTrue(client.move(Direction.toByte(Direction.Right)));
            client.location = client.whereAmI();

            assertEquals(predictNext,client.location);
        }

        // Turn client to face down
        _testMC._SystemTime += 1025;
        while(Direction.fromByte(client.direction) != Direction.Down) {
            _testMC._SystemTime += 1025;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        // Move vertically following time delay restrictions, w/wraparound
        for(int i=0;i<32;i++){
            _testMC._SystemTime += 1025;

            long prevLocation = client.location;
            long predictNext = prevLocation + FIELD_DIM;

            if(predictNext >= (FIELD_DIM*FIELD_DIM))
                predictNext = predictNext % FIELD_DIM;

            client.move(Direction.toByte(Direction.Down));
            client.location = client.whereAmI();

            assertEquals(predictNext,client.location);
        }

        long curLocation = client.whereAmI();

        // Move and expect no change
        _testMC._SystemTime += 975;
        client.move(Direction.toByte(Direction.Right));
        assertEquals(curLocation,client.whereAmI());
        client.move(Direction.toByte(Direction.Up));
        assertEquals(curLocation,client.whereAmI());

        client.leaveGame();
    }

    @Test
    public void t09_Move_TimedSoldierHillTerrain_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(2);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Debris 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Open 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Debris 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Open 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));    // Enter Debris 103->104
        assertEquals(104,client.whereAmI());
    }

    @Test
    public void t10_Move_TimedSoldierDebrisTerrain_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(3);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Hill 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertFalse(client.move(Direction.toByte(Direction.Right)));     // Leave Hill Delay
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Open 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Hill 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertFalse(client.move(Direction.toByte(Direction.Right)));    // Leave Hill Delay
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));    // Enter Open 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter Hill 103->104
        assertEquals(104,client.whereAmI());
    }

    @Test
    public void t11_Fire_TimedSoldier_FiresCorrectDelaysLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(6);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(119);
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedFireInterval + 25;
        int underDelay = client.allowedFireInterval -25;

        // Client joins
        assertEquals(201,client.joinGame());

        // Turn client to face up
        _testMC._SystemTime += 525;
        while(Direction.fromByte(client.direction) != Direction.Up) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        // Client fires following limits and delays
        _testMC._SystemTime += overDelay;

        assertTrue(client.fire(client.direction));

        _testMC._SystemTime += underDelay;

        assertFalse(client.fire(client.direction));

        _testMC._SystemTime += overDelay;

        assertTrue(client.fire(client.direction));

        client.leaveGame();
    }

    @Test
    public void t12_Move_TimedTankAntiGravMultipleTerrains_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(5);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += 260;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter debris 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += 260;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += 260;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter hill 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += 260;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 103->104
        assertEquals(104,client.whereAmI());
    }

    @Test
    public void t13_Fire_TimedTankAntiGrav_FiresCorrectLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(5);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(100,client.whereAmI());

        // Client fires following limits and delays
        _testMC._SystemTime += 275;

        assertTrue(client.fire(Direction.toByte(Direction.Right)));

        _testMC._SystemTime += 225;

        assertFalse(client.fire(Direction.toByte(Direction.Right)));

        client.leaveGame();
    }

    @Test
    public void t14_Move_TimedSoldierAntiGravMultipleTerrains_MovesCorrectDelays()
            throws Exception {
        repository.setPlayerJoinType(2);
        _testMC._SystemTime = 0;
        repository.setMap(5);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedMoveInterval + 25;
        int underDelay = client.allowedMoveInterval -25;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += underDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter debris 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += underDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += underDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter hill 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += underDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 103->104
        assertEquals(104,client.whereAmI());
    }

    @Test
    public void t15_Fire_TimedSoldierAntiGrav_FiresCorrectLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(5);
        repository.disablePowerups();
        repository.setDebugJoinLocation(99);
        repository.setPlayerJoinType(2);
        repository.enableDebugJoin();

        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedFireInterval + 25;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += 1025;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100

        // Face client down
        while(Direction.fromByte(client.direction) != Direction.Down) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += overDelay;

        // Client fires following limits and delays
        assertTrue(client.fire(Direction.toByte(Direction.Right)));

        _testMC._SystemTime += overDelay;

        assertFalse(client.fire(Direction.toByte(Direction.Right)));

        client.leaveGame();
    }

    @Test
    public void t16_Move_TimedTankFusionReactMultipleTerrains_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(4);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += 660;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter debris 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += 660;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += 660;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter hill 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += 660;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 103->104
        assertEquals(104,client.whereAmI());

        _testMC._SystemTime += 525;

        assertFalse(client.move(Direction.toByte(Direction.Right)));     // Enter hill 104->105
        assertNotEquals(105,client.whereAmI());
    }

    @Test
    public void t17_Fire_TimedTankFusionReact_FiresCorrectLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(4);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,1);

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += 525;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(100,client.whereAmI());

        // Face client down
        while(Direction.fromByte(client.direction) != Direction.Down) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += 525;

        // Client fires following limits and delays
        assertTrue(client.fire(Direction.toByte(Direction.Down)));

        _testMC._SystemTime += 225;

        assertFalse(client.fire(Direction.toByte(Direction.Down)));

        client.leaveGame();
    }

    @Test
    public void t18_Move_TimedSoldierFusionReactMultipleTerrains_MovesCorrectDelays()
            throws Exception {
        repository.setPlayerJoinType(2);
        _testMC._SystemTime = 0;
        repository.setMap(4);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedMoveInterval + 275;
        int underDelay = client.allowedMoveInterval + 225;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(99,client.location);

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(100,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter debris 100->101
        assertEquals(101,client.whereAmI());

        _testMC._SystemTime += overDelay;
        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 101->102
        assertEquals(102,client.whereAmI());

        _testMC._SystemTime += overDelay;


        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter hill 102->103
        assertEquals(103,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 103->104
        assertEquals(104,client.whereAmI());

        _testMC._SystemTime += underDelay;

        assertFalse(client.move(Direction.toByte(Direction.Right)));     // Enter hill 104->105
        assertNotEquals(105,client.whereAmI());
    }

    @Test
    public void t19_Fire_TimedSoldierFusionReact_FiresCorrectLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(4);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedFireInterval - 25;
        int underDelay = client.allowedFireInterval - 75;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100

        // Face client down
        while(Direction.fromByte(client.direction) != Direction.Down) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += overDelay;

        // Client fires following limits and delays
        assertTrue(client.fire(Direction.toByte(Direction.Down)));

        _testMC._SystemTime += underDelay;

        assertTrue(client.fire(Direction.toByte(Direction.Down)));

        client.leaveGame();
    }

    @Test
    public void t20_Turn_TimedShip_TurnsCorrectDirectionDelays()
            throws Exception {
        repository.setPlayerJoinType(4);

        _testMC._SystemTime = 0;
        repository.setMap(7);
        repository.enableDebugJoin();
        repository.disablePowerups();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,4);

        int overDelay = client.allowedMoveInterval + 25;
        int underDelay = client.allowedMoveInterval - 25;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        // Clockwise, following time delay restrictions
        for(int i=0;i<4;i++) {
            // ELAPSED_TIME -> 500ms -> 1000ms -> ...
            _testMC._SystemTime += overDelay;

            byte prevDirection = client.direction;

            client.turn((byte) ((prevDirection + 2) % 8));

            assertEquals((prevDirection) % 8, client.direction);
        }

        // AntiClockwise, following time delay restrictions
        for(int i=0;i<4;i++){
            // ELAPSED_TIME
            _testMC._SystemTime += overDelay;

            byte prevDirection = client.direction;

            client.turn((byte)(Math.abs(prevDirection - 2)%8));

            assertEquals(Math.abs(prevDirection) % 8,client.direction);
        }

        // Clockwise, attempting to move before delay
        byte curDirection = client.direction;
        byte nextDirection = (byte)((curDirection + 2) % 8);

        // Turn and expect no change
        _testMC._SystemTime += underDelay;
        client.turn(nextDirection);
        assertNotEquals(curDirection,nextDirection);
    }

    @Test
    public void t21_Move_TimedShipOpenWater_MovesCorrectDelays()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(7);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,4);

        int overDelay = client.allowedMoveInterval + 25;
        int underDelay = client.allowedMoveInterval - 25;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        client.location = client.whereAmI();

        // Move right following time delay restrictions
        for(int i=0;i<4;i++){
            _testMC._SystemTime += overDelay;

            long prevLocation = client.location;
            long predictNext = prevLocation + 1;

            if((predictNext % FIELD_DIM) == 0)
                predictNext = prevLocation - (prevLocation % FIELD_DIM);

            client.move(client.direction);
            client.location = client.whereAmI();
        }

        long curLocation = client.whereAmI();

        // Move and expect no change
        _testMC._SystemTime += underDelay;
        client.move(Direction.toByte(Direction.Right));
        assertEquals(curLocation,client.whereAmI());
        client.move(Direction.toByte(Direction.Up));
        assertEquals(curLocation,client.whereAmI());

        client.leaveGame();
    }

    @Test
    public void t22_Fire_TimedShip_FiresCorrectDelaysLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(7);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,4);

        int overDelay = client.allowedFireInterval + 25;
        int underDelay = client.allowedFireInterval -25;

        // Client joins
        assertEquals(201,client.joinGame());

        // Client fires following limits and delays
        _testMC._SystemTime += overDelay;

        // fires
        assertTrue(client.fire(2));

        // fails to fire - time delay
        assertFalse(client.fire(4));

        // fails to fire - bullet limit
        assertFalse(client.fire(6));

        client.leaveGame();
    }

    @Test
    public void t23_Move_TimedSoldierOpenWaterAndCoast_TurnsMovesCorrectDelays()
            throws Exception {
        repository.setPlayerJoinType(2);
        _testMC._SystemTime = 0;
        repository.setMap(7);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(177);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedMoveInterval + 275;
        int underDelay = client.allowedMoveInterval + 225;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(177,client.location);

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(178,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertFalse(client.move(Direction.toByte(Direction.Right)));     // Enter debris 100->101
        assertEquals(178,client.whereAmI());

        _testMC._SystemTime += overDelay;
        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 101->102
        assertEquals(179,client.whereAmI());
    }

    @Test
    public void t24_Fire_TimedSoldierOpenWaterAndCoast_FiresCorrectLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(7);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedFireInterval - 25;
        int underDelay = client.allowedFireInterval - 75;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100

        // Face client down
        while(Direction.fromByte(client.direction) != Direction.Down) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += overDelay;

        // Client fires following limits and delays
        assertTrue(client.fire(Direction.toByte(Direction.Down)));

        _testMC._SystemTime += underDelay;

        assertFalse(client.fire(Direction.toByte(Direction.Down)));

        client.leaveGame();
    }

    @Test
    public void t25_Move_TimedSoldierOpenWaterAndCoast_TurnsMovesCorrectDelays()
            throws Exception {
        repository.setPlayerJoinType(2);
        _testMC._SystemTime = 0;
        repository.setMap(7);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(177);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedMoveInterval + 275;
        int underDelay = client.allowedMoveInterval + 225;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        client.location = client.whereAmI();
        assertEquals(177,client.location);

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100
        assertEquals(178,client.whereAmI());

        _testMC._SystemTime += overDelay;

        assertFalse(client.move(Direction.toByte(Direction.Right)));     // Enter debris 100->101
        assertEquals(178,client.whereAmI());

        _testMC._SystemTime += overDelay;
        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 101->102
        assertEquals(179,client.whereAmI());
    }

    @Test
    public void t26_Fire_TimedSoldierPowerPackOpenWaterAndCoast_FiresCorrectLimits()
            throws Exception {
        _testMC._SystemTime = 0;
        repository.setMap(7);
        repository.disablePowerups();
        repository.enableDebugJoin();
        repository.setDebugJoinLocation(99);
        repository.setPlayerJoinType(2);

        GameStateStub gameState = new GameStateStub(mockMvc);
        ClientStub client = new ClientStub(mockMvc,2);

        int overDelay = client.allowedFireInterval - 25;
        int underDelay = client.allowedFireInterval - 75;

        // Client joins
        assertEquals(201,client.joinGame());
        client.direction = client.whichWay();

        while(Direction.fromByte(client.direction) != Direction.Right) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += overDelay;

        assertTrue(client.move(Direction.toByte(Direction.Right)));     // Enter power-up 99->100

        // Face client down
        while(Direction.fromByte(client.direction) != Direction.Down) {
            _testMC._SystemTime += 525;
            client.turn((byte) ((client.direction + 2) % 8));
            client.direction = client.whichWay();
        }

        _testMC._SystemTime += overDelay;

        // Client fires following limits and delays
        assertTrue(client.fire(Direction.toByte(Direction.Down)));

        _testMC._SystemTime += underDelay;

        assertFalse(client.fire(Direction.toByte(Direction.Down)));

        client.leaveGame();
    }
}