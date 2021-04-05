package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;

import java.lang.reflect.Field;
import java.rmi.MarshalException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.ValidationEvent;

import edu.unh.cs.cs619.bulletzone.model.terrain.Empty;
import edu.unh.cs.cs619.bulletzone.model.terrain.OpenWater;
import edu.unh.cs.cs619.bulletzone.util.ValueGenerator;

/**
 * <h1> Game Class! </h1>
 * This class sets field dimensions
 * @author given
 * @version 1.0
 * @since Halloween
 */
public final class Game {
    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;
    private final long id;
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();
    private int[] tanksAlive = new int[32];

    private final ConcurrentMap<Long, Tank> tanks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> playersIP = new ConcurrentHashMap<>();

    private ValueGenerator valueGenerator = ValueGenerator.getInstance();

    private final Object monitor = new Object();

    public Game() {
        this.id = 0;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    /**
     * This method adds a tank to the grid.
     * @param ip
     * @param tank
     */
    public void addTank(String ip, Tank tank) {
        synchronized (tanks) {
            tanks.put(tank.getId(), tank);
            playersIP.put(ip, tank.getId());
        }
    }

    public Tank getTank(long tankId) {
        return tanks.get(tankId);
    }

    public ConcurrentMap<Long, Tank> getTanks() {
        return tanks;
    }

    /**
     * This method gets a copy of the grid.
     * @return
     */
    public List<Optional<FieldEntity>> getGrid() {
        synchronized (holderGrid) {
            List<Optional<FieldEntity>> entities = new ArrayList<Optional<FieldEntity>>();

            FieldEntity entity;
            for (FieldHolder holder : holderGrid) {
                if (holder.isPresent()) {
                    entity = holder.getEntity();
                    entity = entity.copy();

                    entities.add(Optional.<FieldEntity>of(entity));
                } else {
                    entities.add(Optional.<FieldEntity>absent());
                }
            }
            return entities;
        }
    }

    /**
     * This method loops through to find the position of the tank.
     * @param ip tank id
     * @return position of tank
     */
    public int getPlayerLocation(String ip) {
        Tank _playerTank = getTank(ip);

        synchronized (holderGrid){
            int index = 0;
            for(FieldHolder holder : holderGrid) {
                if (holder.isPresent()){
                    if(holder.getEntity() == _playerTank)
                        return index;
                }
                index += 1;
            }
        }
        return -1;
    }

    /**
     * This method loops through to find the position of the bullet.
     * @param b instance of bullet
     * @return position of bullet
     */
    public int getBulletLocation(Bullet b) {

        synchronized (holderGrid){
            int index = 0;
            for(FieldHolder holder : holderGrid) {
                if (holder.isPresent()){
                    if(holder.getEntity() == b)
                        return index;
                }
                index += 1;
            }
        }

        return -1;
    }

    private void getTanksAlive(){
        for(int i=0; i<16; i++ ){
            if(tanks.get((long)i) != null){
                tanksAlive[i] = 1;
                //System.out.println("get tanks alive tank alive " + i);
            }
            else {
                tanksAlive[i] = 0;
            }
            if(tanks.get((long)i*10) != null){tanksAlive[i*2] = 1;}
            else{
                tanksAlive[i*2] = 0;
            }
        }
    }


    /**
     * This method gets the tank instance.
     * @param ip
     * @return tank
     */
    public Tank getTank(String ip){
        if (playersIP.containsKey(ip)){
            return tanks.get(playersIP.get(ip));
        }
        return null;
    }

    /**
     * This method removes the tank from the grid.
     * @param tankId unique specifier
     */
    public void removeTank(long tankId){
        synchronized (tanks) {
            Tank t = tanks.remove(tankId);
            t.clearBullets();
            //remove remote controls from vehicles
            if(getTank(tankId*10) != null && getTank(tankId*10).getId() == tankId*10){
                getTank(tankId*10).getRemote().eject();
                //NOTSURE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                getTank(tankId*10).setRemote(null);

                getTank(tankId*10).ejectPowerUp();
            }

            if (t != null) {
                playersIP.remove(t.getIp());
            }
        }
            // check for empty tank and empty ship
            // add if missing
            if (!this.hasEmptyTankWithIdentifier(3))
                this.addEmptyVehicle(3);

            if (!this.hasEmptyTankWithIdentifier(5))
                this.addEmptyVehicle(5);
    }

    /**
     * This method gets the current state of the grid.
     * @return grid // the actual grid
     */
    public int[][] getGrid2D() {
        int[][] grid = new int[FIELD_DIM+2][FIELD_DIM];
        getTanksAlive();

        synchronized (holderGrid) {
            FieldHolder holder;
            for (int i = 0; i < FIELD_DIM+2; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    if(i == FIELD_DIM){
                        grid[i][j] = tanksAlive[j];
                        if(grid[i][j] != 0){
                           //System.out.println("tank alive " + j);
                        }
                    }
                    else if(i == FIELD_DIM+1){
                        grid[i][j] = tanksAlive[j*2];
                    }
                    else {
                        holder = holderGrid.get(i * FIELD_DIM + j);
                        if (holder.isPresent()) {
                            grid[i][j] = holder.getEntity().getIntValue();
                        } else {
                            grid[i][j] = 0;
                        }
                    }
                }
            }
        }
        return grid;
    }

    /**
     * This method provides the arithmetic for if we should make a new power up or not
     * @return boolean
     */
    public boolean shouldWeMakePowerUp() {
        double numberTanks = 0.0;
        double numberPowerUps = 0.0;

        int[][] currentGrid = getGrid2D();
        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 16; j++) {
                if(currentGrid[i][j] == 4600001 || currentGrid[i][j] == 4600002 || currentGrid[i][j] == 4600003)
                    numberPowerUps += 1.0;
                else if(currentGrid[i][j] >= 1000000 && currentGrid[i][j] < 2000000)
                    numberTanks += 1.0;
                else if(currentGrid[i][j] >= 10000000 && currentGrid[i][j] < 20000000)
                    numberTanks += 1.0;
            }
        }

        double probability = (0.25 * numberTanks) / (numberPowerUps + 1.0);
        double randomProbability = new Random().nextDouble() * 3.5;

        return probability >= randomProbability && numberPowerUps <= 10;
    }

    // make empty tank with the ^ code like that

    /**
     * This method gets a free spot in the grid for a power up
     * @return int
     */
    public int getFreeGridLocation() {
        int[][] currentGrid = getGrid2D();
        boolean isValidLocation = false;
        int location = 0;
        Random r = new Random();

        while(!isValidLocation) {
            location = r.nextInt(256);
            int x = location / 16;
            int y = location % 16;

            if(currentGrid[x][y] == 0 || currentGrid[x][y] == 4800000) // empty or coast
                isValidLocation = true;
        }
        return location;
    }

    public void addEmptyVehicle(int type){
        Tank emptyVehicle = null;
        FieldHolder checkCell = null;

        if(type == 3){
            checkCell = getFieldHolder("Empty");
            if(checkCell != null){
                long rID = valueGenerator.getNextRestrictedID();
                emptyVehicle = new Tank(rID,
                        Direction.Up,
                        Long.toString(rID),
                        3);
                System.out.println("Adding empty tank!");
            }
        }
        else if(type == 5)
        {
            checkCell = getFieldHolder("OpenWater");
            if(checkCell != null){
                long rID = valueGenerator.getNextRestrictedID();
                emptyVehicle = new Tank(rID,
                        Direction.Up,
                        Long.toString(rID),
                        5);
                System.out.println("Adding empty ship!");
            }
        }

        if(emptyVehicle != null){
            emptyVehicle.setChild(checkCell.getEntity());
            checkCell.setFieldEntity(emptyVehicle);
            emptyVehicle.setParent(checkCell);

            this.addTank(emptyVehicle.getIp(),emptyVehicle);
        }
    }

    public boolean hasEmptyTankWithIdentifier(int identifier){
        for (Tank tank : tanks.values()) {
            if (tank.getIdentifier() == identifier) {
                return true;
            }
        }

        return false;
    }

    public FieldHolder getFieldHolder(String type){
        FieldHolder nextField = null;
        synchronized (this.monitor) {
            for (int i = 0; i < 256; i++) {
                int nextI = valueGenerator.getNextRandomIndex();
                nextField = this.getHolderGrid().get(nextI);

                if (type.equals("Empty")) {
                    if (nextField.getEntity() instanceof Empty) {
                        System.out.println("Found empty FieldHolder, Try: " + i + " Loc: " + nextI);
                        break;
                    }
                } else if (type.equals("OpenWater")) {
                    if (nextField.getEntity() instanceof OpenWater) {
                        System.out.println("Found empty FieldHolder, Try: " + i + " Loc: " + nextI);
                        break;
                    }
                }
            }
        }

        return nextField;
    }
}