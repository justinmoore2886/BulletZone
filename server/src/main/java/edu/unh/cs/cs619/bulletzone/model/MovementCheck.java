package edu.unh.cs.cs619.bulletzone.model;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class MovementCheck {

    private Transformer mTransformer = new Transformer();
    public long _SystemTime = 0;
    boolean isCompleted;

    /**
     * This method checks time constraints on vehicle
     * @param tank The users vehicle
     * @param use Type of action
     * @return time in between
     */
    public long timecheck(Tank tank, String use) {
        long millis = System.currentTimeMillis();
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

    public void shipTurn(Tank tank, Direction direction){

    }

    /**
     * Turning, check if the time is allow by calling time check then set new direction
     * lastly it sets the new allow time
     * @param tank the tank being checked
     * @param direction NEW direction to be turn to
     * @return if the turn was successful
     */
    public boolean turnCheck(Tank tank, Direction direction) {
        if(tank.getIdentifier() == 2) {
            tank.setDirection(direction);
            return true;
        }
        if (timecheck(tank, "move") != 0 && tank.getIdentifier() < 4) {
            if (Math.abs(Direction.toByte(tank.getDirection()) - Direction.toByte(direction)) != 4) {
                tank.setDirection(direction);
                tank.setLastMoveTime(timecheck(tank, "move") + tank.getAllowedMoveInterval());
                return true;
            } else {
                return false;
            }
        } else if (timecheck(tank, "turn") != 0 && tank.getIdentifier() >= 4) {
            if (Math.abs(Direction.toByte(tank.getDirection()) - Direction.toByte(direction)) != 4) {
                tank.setDirection(direction);
                if(System.currentTimeMillis()-tank.getLastMoveTime() < 1000) {
                    tank.setLastTurnTime(timecheck(tank, "move") + ShipInfo.allowedTurnInterval);
                } else {
                    tank.setLastTurnTime(timecheck(tank, "move") + 400);
                }
                return true;
            } else {
                return false;
            }
        } else { return false;}
    }

    /**
     * moving, check if the time is allow by calling time check then set location
     * lastly it sets the new allow time. It also handles the tank through terrain scenario
     * and sets power-ups.
     * @param tank
     * @param direction
     * @param game
     * @return
     */

    public boolean moveCheck(Tank tank, Direction direction, Game game) {
        if (timecheck(tank, "move") != 0) {
            //ship not moving backwards
            if ((Math.abs(Direction.toByte(tank.getDirection()) - Direction.toByte(direction)) == 4)
                    && (tank.getIdentifier() > 3)) {
                return false;
            }

            FieldHolder parent = tank.getParent();
            FieldEntity child = tank.getChild();
            FieldHolder nextField = parent.getNeighbor(direction);

            //store control tank's control id
            long tankInitialControlsId = tank.getControlsId();
            long nextInitialControllersId = -1;
            if(nextField.getEntity().toString().equals("T")){
                nextInitialControllersId = ((Tank) nextField.getEntity()).getId();
            }
            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

            isCompleted = nextField.getEntity().action(tank);
            //if next field is not an object, move onto it
            if(isCompleted) {
                parent.clearField();
                parent.setFieldEntity(child);
                nextField.clearField();
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);
                tank.setLastMoveTime(timecheck(tank, "move") + tank.getAllowedMoveInterval());
                System.out.println("tank moves into terrain or powerup");
                System.out.println("moved now tank id ");
            }

            else if((nextField.getEntity() instanceof Tank && System.currentTimeMillis() > tank.getLastEnterTime() &&
                    (((Tank) nextField.getEntity()).getIdentifier() == 3||((Tank) nextField.getEntity()).getIdentifier() == 5) && tank.getIdentifier() == 2)){
                /**
                 * Re-entry Code
                 * if the nextField is an empty tank and time allowed is met, "transform" the soldier to a tank and replace the empty tank
                 */
                Tank emptyTank = (Tank) nextField.getEntity();
                int wasType = emptyTank.getIdentifier();

                tank.clearBullets();
                mTransformer.SoldiertoTank(tank, emptyTank);
                tank.setLastEnterTime(System.currentTimeMillis() + tank.getAllowedReEnterTime());
                System.out.println("empty tank ID removed: " + (100 - tank.getId()));
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);
                parent.setFieldEntity(tank.getChild());
                tank.setChild(emptyTank.getChild());
                tank.setLastMoveTime(timecheck(tank, "move") + tank.getAllowedMoveInterval());
                tank.setLastTurnTime(tank.getLastMoveTime());
                tank.setOwnerId(tank.getId());

                // If no empty tanks, make another!
                if(!game.hasEmptyTankWithIdentifier(wasType))
                    game.addEmptyVehicle(wasType);

                isCompleted = true;
            }

            //if is completed false, control id of tank(soldier) has changed
            else if(!isCompleted && tank.getControlsId() != tankInitialControlsId) {
                //map the tank to its new id in the game tank array
                game.addTank("not new user", (Tank)nextField.getEntity());
                System.out.println("just took control of enemy tank");
            }
            //means soldier forced a remote control to eject from tank
           /* else if(!isCompleted && nextField.getEntity().toString().equals("T")) {
                if (((Tank) nextField.getEntity()).getId() != nextInitialControllersId) {
                    //game.removeTank(((Tank) nextField.getEntity()).getId());
                    game.getTank(((Tank) nextField.getEntity()).getControllerId()).setControls(-1);
                    ((Tank) nextField.getEntity()).setControllerId(-1);
                    //tank is now empty
                    System.out.println("empty tank ID removed: " + (100 - tank.getId()));
                    //remove controlled mapped tank
                }
            }*/
            return isCompleted;
        }
        else {return false;}
    }
}
