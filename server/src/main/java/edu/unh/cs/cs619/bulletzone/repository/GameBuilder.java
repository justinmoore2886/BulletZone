package edu.unh.cs.cs619.bulletzone.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.powerups.AntiGravity;
import edu.unh.cs.cs619.bulletzone.model.powerups.PowerRack;
import edu.unh.cs.cs619.bulletzone.model.terrain.Coast;
import edu.unh.cs.cs619.bulletzone.model.terrain.DebrisField;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.terrain.Empty;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.powerups.FusionReactor;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.terrain.Hill;
import edu.unh.cs.cs619.bulletzone.model.terrain.OpenWater;
import edu.unh.cs.cs619.bulletzone.model.Wall;

public class GameBuilder {
    private static Game game;
    private static int FIELD_DIM = 16;

    // Generates a random Set of 256 unique gridCell indices 0-255
    private static Set<Integer> randomCellSet = new HashSet<>(256);

    /**
     * This method builds a blank map
     * @return game
     */
    public static Game BuildBlankMap() {
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }
        return game;
    }

    /**
     * This method builds a map with hills.
     * @return
     */
    public static Game BuildHillMap(){
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }

        game.getHolderGrid().get(100).setFieldEntity(new Hill());
        game.getHolderGrid().get(102).setFieldEntity(new Hill());
        game.getHolderGrid().get(104).setFieldEntity(new Hill());

        return game;
    }

    /**
     * This method builds a map with debris fields.
     * @return
     */
    public static Game BuildDebrisMap(){
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }

        game.getHolderGrid().get(100).setFieldEntity(new DebrisField());
        game.getHolderGrid().get(102).setFieldEntity(new DebrisField());
        game.getHolderGrid().get(104).setFieldEntity(new DebrisField());

        return game;
    }

    /**
     * This method builds a map with fusion reactors.
     * @return
     */
    public static Game BuildFusionReactorMap(){
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }

        game.getHolderGrid().get(100).setFieldEntity(new FusionReactor(100));
        game.getHolderGrid().get(101).setFieldEntity(new DebrisField());
        game.getHolderGrid().get(102).setFieldEntity(new FusionReactor(102));
        game.getHolderGrid().get(103).setFieldEntity(new Hill());
        game.getHolderGrid().get(104).setFieldEntity(new FusionReactor(104));

        return game;
    }

    /**
     * This method builds a map with anti gravity power-ups.
     * @return
     */
    public static Game BuildAntiGravityMap(){
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }

        game.getHolderGrid().get(100).setFieldEntity(new AntiGravity(100));
        game.getHolderGrid().get(101).setFieldEntity(new DebrisField());
        game.getHolderGrid().get(102).setFieldEntity(new AntiGravity(102));
        game.getHolderGrid().get(103).setFieldEntity(new Hill());
        game.getHolderGrid().get(104).setFieldEntity(new AntiGravity(104));
        game.getHolderGrid().get(103).setFieldEntity(new Hill());

        game.getHolderGrid().get(109).setFieldEntity(new Wall());
        game.getHolderGrid().get(125).setFieldEntity(new Wall());
        game.getHolderGrid().get(141).setFieldEntity(new Wall());
        game.getHolderGrid().get(157).setFieldEntity(new Wall());
        game.getHolderGrid().get(173).setFieldEntity(new Wall());

        return game;
    }

    public static Game BuildFireTestMap(){
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }

        game.getHolderGrid().get(134).setFieldEntity(new Wall());
        game.getHolderGrid().get(136).setFieldEntity(new Wall());
        game.getHolderGrid().get(150).setFieldEntity(new Wall());
        game.getHolderGrid().get(151).setFieldEntity(new Wall());
        game.getHolderGrid().get(152).setFieldEntity(new Wall());

        return game;
    }

    public static Game BuildWaterWorld(){
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }

        for(int i=0; i<176;i++){
            game.getHolderGrid().get(i).setFieldEntity(new OpenWater());
        }
        for(int i=176;i<256;i++){
            game.getHolderGrid().get(i).setFieldEntity(new Coast());
        }

        return game;
    }

    /**
     * This method builds a default map.
     * @return
     */
    public static Game BuildDefaultMap(){
        game = new Game();
        createFieldHolderGrid(game);
        for(int i = 0; i < 256; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Empty());
        }

        // Test // TODO XXX Remove & integrate map loader
        game.getHolderGrid().get(1).setFieldEntity(new Wall());
        game.getHolderGrid().get(2).setFieldEntity(new Wall());
        game.getHolderGrid().get(3).setFieldEntity(new Wall());

        game.getHolderGrid().get(17).setFieldEntity(new Wall());
        game.getHolderGrid().get(33).setFieldEntity(new Wall(1503, 33));
        game.getHolderGrid().get(49).setFieldEntity(new Wall(1503, 49));
        game.getHolderGrid().get(65).setFieldEntity(new Wall(1503, 65));

        game.getHolderGrid().get(34).setFieldEntity(new Wall());
        game.getHolderGrid().get(66).setFieldEntity(new Wall(1503, 66));

        game.getHolderGrid().get(35).setFieldEntity(new Wall());
        game.getHolderGrid().get(51).setFieldEntity(new Wall());
        game.getHolderGrid().get(67).setFieldEntity(new Wall(1503, 67));

        game.getHolderGrid().get(5).setFieldEntity(new Wall());
        game.getHolderGrid().get(21).setFieldEntity(new Wall());
        game.getHolderGrid().get(37).setFieldEntity(new Wall());
        game.getHolderGrid().get(53).setFieldEntity(new Wall());
        game.getHolderGrid().get(69).setFieldEntity(new Wall(1503, 69));
        game.getHolderGrid().get(7).setFieldEntity(new Wall());
        game.getHolderGrid().get(23).setFieldEntity(new Wall());
        game.getHolderGrid().get(39).setFieldEntity(new Wall());
        game.getHolderGrid().get(71).setFieldEntity(new Wall(1503, 71));

        game.getHolderGrid().get(8).setFieldEntity(new Wall());
        game.getHolderGrid().get(40).setFieldEntity(new Wall());
        game.getHolderGrid().get(72).setFieldEntity(new Wall(1503, 72));

        game.getHolderGrid().get(9).setFieldEntity(new Wall());
        game.getHolderGrid().get(25).setFieldEntity(new Wall());
        game.getHolderGrid().get(41).setFieldEntity(new Wall());
        game.getHolderGrid().get(57).setFieldEntity(new Wall());
        game.getHolderGrid().get(73).setFieldEntity(new Wall());

        // Added Hills
        game.getHolderGrid().get(83).setFieldEntity(new Hill(83));
        game.getHolderGrid().get(82).setFieldEntity(new Hill(82));

        // Added Debris Fields
        game.getHolderGrid().get(114).setFieldEntity(new DebrisField(114));
        game.getHolderGrid().get(115).setFieldEntity(new DebrisField(115));

        // Added Coast
        for(int i = 144; i < 176; i++){
            game.getHolderGrid().get(i).setFieldEntity(new OpenWater(i));
        }
        for(int i = 128; i < 144; i++) {
            game.getHolderGrid().get(i).setFieldEntity(new Coast(i));
        }
        for(int i = 176; i < 192; i++){
            game.getHolderGrid().get(i).setFieldEntity(new Coast(i));
        }

        game.addEmptyVehicle(3);
        game.addEmptyVehicle(5);

        return game;
    }

    /**
     * This method creates a base field holder grid.
     * @param game
     */
    private static void createFieldHolderGrid(Game game) {
        {
            game.getHolderGrid().clear();
            for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
                game.getHolderGrid().add(new FieldHolder());
            }

            while(randomCellSet.size() < 256){
                randomCellSet.add((int)(Math.random() * 256));
            }

            FieldHolder targetHolder;
            FieldHolder rightHolder;
            FieldHolder downHolder;

            // Build connections between grid cells
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    targetHolder = game.getHolderGrid().get(i * FIELD_DIM + j);
                    rightHolder = game.getHolderGrid().get(i * FIELD_DIM + ((j + 1) % FIELD_DIM));
                    downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM) * FIELD_DIM + j);

                    targetHolder.addNeighbor(Direction.Right, rightHolder);
                    rightHolder.addNeighbor(Direction.Left, targetHolder);

                    targetHolder.addNeighbor(Direction.Down, downHolder);
                    downHolder.addNeighbor(Direction.Up, targetHolder);
                }
            }

            for(int i = 0; i < 256; i++){
                game.getHolderGrid().get(i).setFieldEntity(new Empty());
            }
        }
    }
}