package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.ShipInfo;
import edu.unh.cs.cs619.bulletzone.model.SoldierInfo;
import edu.unh.cs.cs619.bulletzone.model.powerups.AntiGravity;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerRack;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerUp;
import edu.unh.cs.cs619.bulletzone.model.powerups.RemoteControl;
import edu.unh.cs.cs619.bulletzone.model.terrain.Empty;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.FireBullet;
import edu.unh.cs.cs619.bulletzone.model.powerups.FusionReactor;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.MovementCheck;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.TankInfo;
import edu.unh.cs.cs619.bulletzone.model.Transformer;
import edu.unh.cs.cs619.bulletzone.util.ValueGenerator;

import static com.google.common.base.Preconditions.checkNotNull;
import static sun.security.krb5.Confounder.longValue;

/**
 * <h1> InMemoryGameRepository class! </h1>
 * This class is where all the game stuff is happening.
 * @author given
 * @version 1.0
 * @since Halloween
 */
@Component
public class InMemoryGameRepository implements GameRepository {

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;

    /**
     * Bullet step time in milliseconds
     */
    private static int BULLET_PERIOD = 200;

    /**
     * Bullet's impact effect [life]
     */
    private static final int BULLET_DAMAGE = 1;

    /**
     * Tank's default life [life]
     */
    private static final int TANK_LIFE = 100;
    private static final int DEFAULT_JOIN_METHOD = 1;
    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game;
    private int bulletDamage[]={10,30,50};
    private int bulletDelay[]={500,1000,1500};
    private int trackActiveBullets[]={0,0,0,0,0,0,0,0,0,0,0,0};
    private MovementCheck mMovementCheck = new MovementCheck();
    private FireBullet mFireBullet = new FireBullet();
    private boolean powerUpsDisabled = false;
    private boolean debugJoin = false;
    private int debugJoinLocation = 0;
    private int playerJoinType = 1;
    private Transformer mTransformer = new Transformer();
    private long IdCounter = 0;

    private ValueGenerator valueGenerator = ValueGenerator.getInstance();

    private int mapChoice = 0;

    /**
     * This method lets the user's tank join the game.
     * @param ip
     * @return
     */
    @Override
    public Tank join(String ip, int identifier) {
        playerJoinType = identifier;
        synchronized (this.monitor) {
            Tank tank;
            if (game == null) {
                this.create();
            }

            if( (tank = game.getTank(ip)) != null){
                return tank;
            }
            Long tankId=0L;
            //+1 prevents a zero id that interferes with remote control functionality
            //Long tankId = (this.idGenerator.getAndIncrement()+1)%9;

            //Long tankId = this.idGenerator.getAndIncrement() + 1;
            System.out.println("1tank ID" + tankId+ " " + playerJoinType+"\n");
            if(playerJoinType == 3){
                tankId.valueOf(99);
            } else if(playerJoinType == 5) {
                tankId.valueOf(98);
            }
            if(playerJoinType == 1 || playerJoinType == 4 || playerJoinType == 2){
                IdCounter++;
                tankId = (IdCounter)%10;
                System.out.println("eject called, tank ID: " + tankId + " " + playerJoinType+"\n");
                if(tankId == 0){
                    IdCounter++;
                    tankId = (IdCounter)%10;
                    System.out.println("2tank ID" + tankId+ " " + playerJoinType+"\n");
                }
            }
                System.out.println("3tank ID" + tankId+ " " + playerJoinType+"\n");
            if(playerJoinType == 1 || playerJoinType == 3) {
                tank = new Tank(tankId, Direction.Up, ip, playerJoinType);
                tank.setChild(new Empty());
                tank.setLife(TankInfo.life);
            }
            else if(playerJoinType == 2){
                tank = new Tank(tankId,Direction.Up,ip,playerJoinType);
                tank.setLife(SoldierInfo.life);
            }
            else if(playerJoinType == 4 || playerJoinType == 5){
                tank = new Tank(tankId, Direction.Up, ip, playerJoinType);
                tank.setChild(new Empty());
                tank.setLife(ShipInfo.life);
            }

            if(debugJoin){
                FieldHolder fieldElement = game.getHolderGrid().get(debugJoinLocation);

                fieldElement.setFieldEntity(tank);
                tank.setParent(fieldElement);
            }else{
                Random random = new Random();
                int x;
                int y;

                // This may run for forever.. If there is no free space. XXX
                for (; ; ) {
                    x = random.nextInt(FIELD_DIM);
                    y = random.nextInt(FIELD_DIM);
                    FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);

                    if (/*!fieldElement.isPresent() || */(fieldElement.getEntity().toString() == "E")
                            || (fieldElement.getEntity().toString() == "OW"))
                    {
                        //System.out.println("Try to place at: " + (x * FIELD_DIM + y));
                        if (fieldElement.getEntity().toString() == "OW" && (playerJoinType == 1 || playerJoinType == 3)){
                            continue;
                        }
                        if (fieldElement.getEntity().toString() != "OW" && (playerJoinType == 4 || playerJoinType == 5)) {
                            continue;
                        }
                        tank.setChild(fieldElement.getEntity());
                        fieldElement.setFieldEntity(tank);
                        tank.setParent(fieldElement);
                        break;
                    }
                }
            }
            System.out.println("1tank ID" + tank.getId()+ " " + playerJoinType+"\n");
            game.addTank(ip, tank);

            return tank;
        }
    }

    @Override
    public boolean ejectSoldier(long tankId)
            throws TankDoesNotExistException{
        Tank tank = game.getTanks().get(tankId);
        if (tank == null) {
            throw new TankDoesNotExistException(tankId);
        }
        if(System.currentTimeMillis() > tank.getLastEnterTime() && (tank.getIdentifier()== 1 || tank.getIdentifier()==4)) {
            synchronized (this.monitor) {
                if (!this.game.getTanks().containsKey(tankId)) {
                    throw new TankDoesNotExistException(tankId);
                }
                System.out.println("eject called, tank ID: " + tankId);
                /**
                 * Make a empty tank with info from the player tank to be place at player location
                 */
                Tank emptyTank;
                int newidentifier;
                if (tank.getIdentifier() == 1) {
                    newidentifier = 3;
                } else {
                    newidentifier = 5;
                }
                //consider leaving empty tank id as 100 --------------------------                          ///TODO
                System.out.println("eject tank id" + tank.getId() + " tank ownerid " + tank.getOwnerId()+ " tankid " + tankId + "\n");
                //if tank is controlled and no enemy is inside i.e. id of controlled vehicle == tank's id  -------case: tank is remote controlled with no soldier inside, then do nothing----maybe eject all powerups?
                if((tank.getControllerId() > -1 && tank.getOwnerId() == -1) || tankId != tank.getOwnerId()){return false;}

                //if tank is controlled ie id of controlled vehicle != tank's id    -----case: tank is remote controlled but enemy is inside
                if(tank.getControllerId() > -1 && tank.getOwnerId() > -1){
                   // System.out.println("tank before eject called ownerid " + tank.getOwnerId() + " controllerid " + tank.getControllerId() + " id " + tank.getId() +"\n");
                    emptyTank = (Tank)tank.copy();
                    emptyTank.setRemote((RemoteControl)tank.getRemote().copy());
                    emptyTank.setPowerUp(tank.getRemote());
                    emptyTank.setOwnerId(-1);
                    emptyTank.getRemote().setOwner(emptyTank);
                    emptyTank.setControllerId(emptyTank.getId()/10);
                    tank.setId(tank.getOwnerId());
                    tank.setOwnerId(tank.getId());
                    tank.setControllerId(-1);
                    tank.getRemote().setEjected();
                    game.addTank("", tank);
                    game.addTank("", emptyTank);
                    //System.out.println("tank before eject called ownerid " + tank.getOwnerId() + " controllerid " + tank.getControllerId() + " id " + tank.getId()+"\n");
                    //System.out.println("empty tank before eject called ownerid " + emptyTank.getOwnerId() + " controllerid " + emptyTank.getControllerId() + " id " + emptyTank.getId()+"\n");
                }

                else {
                    long rID = valueGenerator.getNextRestrictedID();
                    emptyTank = new Tank(rID, tank.getDirection(), Long.toString(rID), newidentifier);
                    tank.clearBullets();
                }

               // Tank emptyTank = new Tank(100 - tankId, tank.getDirection(), " ", 3);
                //Tank emptyTank = new Tank(100 - tankId, tank.getDirection(), " ", newidentifier);
                //emptyTank.setAllowedNumberOfBullets(0);
                System.out.println("empty tank ID: " + (98 -tankId));
                emptyTank.setLife(tank.getLife());
                FieldHolder parent = tank.getParent();
                emptyTank.setOwnerId(-1);
                //clear all tank bullets

                byte i = 0;
                while(i <= 6){
                    FieldHolder nextField = parent.getNeighbor(Direction.fromByte(i));
                    if (!nextField.isPresent() || (nextField.getEntity().toString() == "E") || (nextField.getEntity().toString() == "OW")) {
                        emptyTank.setChild(tank.getChild());
                        tank.setChild(nextField.getEntity());
                        nextField.clearField();
                        nextField.setFieldEntity(tank);
                        tank.setParent(nextField);
                        parent.setFieldEntity(emptyTank);
                        emptyTank.setParent(parent);
                        game.addTank(emptyTank.getIp(),emptyTank); // added to track empty tanks in game tankMap
                        tank.setLastEnterTime(System.currentTimeMillis() + TankInfo.allowedReEnterTime);
                        mTransformer.TanktoSoldier(tank);
                        System.out.println("ejected soldier int " + tank.getIntValue() + "\n");
                        System.out.println("NUMBERS FOR SOLDIER " + tank.getAllowedFireInterval() + " " + tank.getAllowedMoveInterval() + " " + tank.getAllowedNumberOfBullets());
                        return true;
                    } else {
                        i += 2;
                    }
                }
                return false;
            }
        }else{return false;}
    }

    /**
     * This method allows the tank to eject a powerup.
     * @param tankId
     * @throws TankDoesNotExistException
     */
    @Override
    public boolean eject(long tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            if(!this.game.getTanks().containsKey(tankId)) {
                throw new TankDoesNotExistException(tankId);
            }

            System.out.println("eject called, tank ID: " + tankId);
            Tank tank = game.getTanks().get(tankId);

            boolean isRack = tank.ejectPowerUp();
            if(isRack)
                SpawnRack(tank);
            return true;
        }
    }

    public void SpawnRack(Tank tank){
        byte i = 0;
        FieldHolder parent = tank.getParent();
        while(i <= 6){
            FieldHolder nextField = parent.getNeighbor(Direction.fromByte(i));
            if (!nextField.isPresent() || (nextField.getEntity().toString() == "E")) {
                nextField.clearField();
                nextField.setFieldEntity(new PowerRack(tank.getPos() + 1));
                return;
            } else {
                i += 2;
            }
        }
        System.out.println("Should have ejected a rack");
    }

    /**
     * This method asks confirms if the tank is the user's tank.
     * @param ip
     * @return
     * @throws TankDoesNotExistException
     */
    @Override
    public Tank whoAmI(String ip) throws TankDoesNotExistException{
        long bad = -1;

        synchronized (this.monitor) {
            if(game == null)
                throw new TankDoesNotExistException(bad);

            Tank tank = game.getTank(ip);

            if (tank == null)
                throw new TankDoesNotExistException(bad);

            return tank;
        }
    }

    /**
     * This method gets the players location.
     * @param ip
     * @return
     * @throws TankDoesNotExistException
     */
    @Override
    public int whereAmI(String ip) throws TankDoesNotExistException{
        long bad = -1;

        synchronized (this.monitor) {
            if(game == null)
                throw new TankDoesNotExistException(bad);

            if(game.getTank(ip) == null)
                throw new TankDoesNotExistException(bad);

            return game.getPlayerLocation(ip);
        }
    }

    /**
     * This method gets the players direction.
     * @param ip
     * @return
     * @throws TankDoesNotExistException
     */
    @Override
    public byte whichWay(String ip) throws TankDoesNotExistException{
        long bad = -1;

        synchronized (this.monitor) {
            if(game == null)
                throw new TankDoesNotExistException(bad);

            Tank tank = game.getTank(ip);

            if (tank == null)
                throw new TankDoesNotExistException(bad);

            return Direction.toByte(tank.getDirection());
        }
    }

    /**
     * This method gets the players amount of bullets.
     * @param ip
     * @return
     * @throws TankDoesNotExistException
     */
    @Override
    public int howManyBullets(String ip) throws TankDoesNotExistException{
        long bad = -1;

        synchronized (this.monitor) {
            if(game == null)
                throw new TankDoesNotExistException(bad);

            Tank tank = game.getTank(ip);

            if (tank == null)
                throw new TankDoesNotExistException(bad);

            return tank.getNumberOfBullets();
        }
    }


    public long amIAlive(long primaryId, long remoteId)throws TankDoesNotExistException{
        long alive = 0;

        if (game.getTanks() == null) {
            throw new TankDoesNotExistException(primaryId);
        }
        if (game.getTanks().get(primaryId) != null && game.getTanks().get(primaryId).getId() == primaryId) {
            alive = 10;
        }
        if (game.getTanks() == null) {
            throw new TankDoesNotExistException(primaryId);
        }
        if (game.getTanks().get(remoteId) != null && game.getTanks().get(remoteId).getId() == remoteId) {
            alive = alive + 1;
        }
        return alive;
    }


    /**
     * This method gets the grid state.
     * @return grid
     */
    @Override
    public int[][] getGrid() {

        // Makes a new power up appear
        if(!powerUpsDisabled) {
            if (game != null && game.shouldWeMakePowerUp()) {
                int location = game.getFreeGridLocation();
                int randomPower = new Random().nextInt(100);
                FieldEntity locationChild = game.getHolderGrid().get(location).getEntity();

                if (randomPower <= 33) {
                    game.getHolderGrid().get(location).setFieldEntity(new FusionReactor(location));
                }
                else if(randomPower > 33 && randomPower <= 66 ) {
                    game.getHolderGrid().get(location).setFieldEntity(new AntiGravity(location));
                }
                else {
                    game.getHolderGrid().get(location).setFieldEntity(new PowerRack(location));
                }
                game.getHolderGrid().get(location).getEntity().setChild(locationChild);
                game.getHolderGrid().get(location).getEntity().setParent(game.getHolderGrid().get(location));
            }
        }

        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getGrid2D();
    }

    /**
     * This method turns the tank in a given direction.
     * @param tankId
     * @param direction
     * @return
     * @throws TankDoesNotExistException
     * @throws IllegalTransitionException
     * @throws LimitExceededException
     */
    @Override
    public boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            checkNotNull(direction);

            // Find user
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                throw new TankDoesNotExistException(tankId);
            }
            //MovementCheck in model folder
            if(tank.getId() != tankId){return false;}
            return mMovementCheck.turnCheck(tank, direction);
        }
    }

    /**
     * This method moves the tank in a certain direction.
     * @param tankId
     * @param direction
     * @return
     * @throws TankDoesNotExistException
     * @throws IllegalTransitionException
     * @throws LimitExceededException
     */
    @Override
    public boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            // Find tank

            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }
            // MovementCheck in model folder


            //add if tankid < 10 and tankiscontrolled           ///TODO
                //return false
            if(tank.getId() != tankId){return false;}
            return mMovementCheck.moveCheck(tank, direction, game);
        }
    }

    /**
     * This method registers a tank "fire".
     * @param tankId
     * @param directionFire
     * @return
     * @throws TankDoesNotExistException
     * @throws LimitExceededException
     */
    @Override
    public boolean fire(long tankId, Direction directionFire)
            throws TankDoesNotExistException, LimitExceededException {
        synchronized (this.monitor) {

            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }

            //add if tankid < 10 and tankiscontrolled       ///TODO
            //return false


            // MovementCheck in model folder
            if(mMovementCheck.timecheck(tank, "bullet") == 0)
                return false;
            System.out.println(tank.getLastFireTime());
            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            if(tank.getId() != tankId){return false;}

            if(tank.getNumberOfBullets() < tank.getAllowedNumberOfBullets()){
                System.out.println("NumOfBullets: " + tank.getNumberOfBullets());
                System.out.println("AllowedNumOfBullets: " + tank.getAllowedNumberOfBullets());

                if(tank.getIdentifier() > 3 && direction != directionFire){
                    if(debugJoin)
                        tank.setLastFireTime(mMovementCheck._SystemTime + tank.getAllowedShipSideFireInterval());
                    else
                        tank.setLastFireTime(System.currentTimeMillis() + tank.getAllowedShipSideFireInterval());
                } else {
                    if(debugJoin)
                        tank.setLastFireTime(mMovementCheck._SystemTime + tank.getAllowedFireInterval());
                    else
                        tank.setLastFireTime(System.currentTimeMillis() + tank.getAllowedFireInterval());
                }

                return(mFireBullet.createBullet(tank, direction, parent, directionFire, trackActiveBullets, bulletDamage, BULLET_PERIOD, game, timer, monitor,debugJoin));
            }


            //tank.setLastFireTime(mMovementCheck.timecheck(tank, "bullet") + bulletDelay[1]);
            //tank.setLastFireTime(mMovementCheck.timecheck(tank, "bullet") + tank.getAllowedFireInterval());
            // FireBullet in model folder
            return false;
        }

    }

    /**
     * This method lets the tank leave the game.
     * @param tankId
     * @throws TankDoesNotExistException
     */
    @Override
    public void leave(long tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            if (!this.game.getTanks().containsKey(tankId)) {
                throw new TankDoesNotExistException(tankId);
            }

            System.out.println("leave() called, tank ID: " + tankId);

            Tank tank = game.getTanks().get(tankId);
            FieldHolder parent = tank.getParent();
            parent.clearField();
            parent.setFieldEntity(tank.getChild());
            game.removeTank(tankId);
        }
    }

    /**
     * This method creates a map / grid
     */
    public void create() {
        if (game != null) {
            return;
        }
        setMap(mapChoice);
    }

    /*
     * Added the following for testing purposes
     */

    /**
     * This method is a default constructor.
     */
    public InMemoryGameRepository() {
    }


    /** This method is an overloaded constructor to allow use of overloaded create
     *
     * @param testMapChoice
     * @param _testMC
     * @param _testFB
     */
    public InMemoryGameRepository(int testMapChoice,MovementCheck _testMC,
                                  FireBullet _testFB, int _playerJoinType) {
        mapChoice = testMapChoice;
        mMovementCheck = _testMC;
        mFireBullet = _testFB;
        playerJoinType = _playerJoinType;
        this.disablePowerups();
    }

    public void setMap(int mapChoice){
        synchronized (this.monitor) {
            switch(mapChoice){
                case 0: game = GameBuilder.BuildDefaultMap();
                        break;
                case 1: game = GameBuilder.BuildBlankMap();
                        break;
                case 2: game = GameBuilder.BuildHillMap();
                        break;
                case 3: game = GameBuilder.BuildDebrisMap();
                        break;
                case 4: game = GameBuilder.BuildFusionReactorMap();
                        break;
                case 5: game = GameBuilder.BuildAntiGravityMap();
                        break;
                case 6: game = GameBuilder.BuildFireTestMap();
                case 7: game = GameBuilder.BuildWaterWorld();
                default: break;
            }
        }
    }

    public Object getMonitor() {
        return this.monitor;
    }

    public void disablePowerups(){
        powerUpsDisabled = true;
    }
    public void enablePowerups(){
        powerUpsDisabled = false;
    }
    public void enableDebugJoin(){debugJoin = true;}
    public void disableDebugJoin(){debugJoin = false;}
    public void setDebugJoinLocation(int loc){debugJoinLocation = loc;}
    public void setPlayerJoinType(int type){playerJoinType = type;}
    public int getPlayerJoinType(){return playerJoinType;}
}
