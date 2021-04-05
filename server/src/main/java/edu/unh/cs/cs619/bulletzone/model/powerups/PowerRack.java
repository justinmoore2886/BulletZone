package edu.unh.cs.cs619.bulletzone.model.powerups;

import java.util.Stack;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public class PowerRack extends PowerUp {
    public Stack<PowerUp> rack = new Stack<>();
    public boolean rackIndicator = false;

    public PowerRack(int pos){
        setPos(pos);
    }

    @Override
    public FieldEntity copy() {
        return new PowerRack(getPos());
    }

    @Override
    public int getIntValue() {
        return (/*10000000000L*rackedSoldier + */4600003);
    }

    @Override
    public String toString() {
        return "PR";
    }

    /**
     * This method applies the power ups effect to the user
     * @param vehicle The users vehicle
     */
    public void applyEffect(Tank vehicle) {
        // Soldier with power rack turns into a wheeled soldier
        if(vehicle.getIdentifier() == 2) {
            vehicle.setAllowedMoveInterval(200);
            vehicle.setAllowedTurnInterval(200);
            System.out.println(vehicle.getAllowedMoveInterval());
            System.out.println(vehicle.getAllowedTurnInterval());
        }
        if(!rackIndicator) { // if its the first power-up, set the Tanks stack
            if (vehicle.getIdentifier() == 2) {System.out.println("set soldier racked ");
                vehicle.setSoldierRacked(5);}
                vehicle.setPowerUpStack(this);
        }
        else { // else apply the current power-ups effects if the rack isnt empty
            PowerUp p = getCurrentPowerup(this);
            System.out.println(p.toString());
            if(p instanceof PowerRack)
                return;
            else
                p.applyEffect(vehicle);
        }
    }

    /**
     * This method adds a power rack or power up to the power rack
     * @param power The power up
     * @param r Powerrack added to
     */
    public void RackAddition(PowerUp power, PowerRack r){
        int i = 0;
        System.out.println("Rack Addition");
        if(r.rack.empty()){ // if the rack is empty
            System.out.println("pushed " + power);
            r.rack.push(power); // push the power up
            return;
        }
        while(i < r.rack.size()) { // while there are power ups in the rack, loop thru them
            if (r.rack.elementAt(i) instanceof PowerRack) { // if one is a power rack, loop thru that
                System.out.println("recursion");
                RackAddition(power, (PowerRack) r.rack.elementAt(i));
            } else if(r.rack.size() < 3){ // push it into current rack
                System.out.println("pushedr " + power);
                r.rack.push(power);
                return;
            }
            i ++;
        }
    }

    /**
     * This method ejects from power rack
     * @param r Powerrack added to
     */
    public boolean RackEjection(PowerRack r){ // returns true if ejected a rack
        if(r.rack.size() == 0){ // if rack is empty, dont do anything
            return false;
        }
        if(r.rack.elementAt(r.rack.size() - 1) instanceof PowerRack){ // if the spot that needs to be ejected is a power rack
            System.out.println("recursion");
            if(((PowerRack) r.rack.elementAt(r.rack.size() - 1)).rack.empty()) { // if that rack is empty, pop it
                PowerUp q= r.rack.pop();
                System.out.println("Popped " + q);
                return true;
            } // otherwise, check for open spot in that rack
            boolean isRack = RackEjection((PowerRack) r.rack.elementAt(r.rack.size() - 1)); // find the open spot in THAT power rack
            if(isRack)
                return true;
            else
                return false;
        }
        else { // if the spot that needs to be ejected is a powerup, pop it
            PowerUp q;
            System.out.println("rack size " + rack.size());
            if(r.rack.elementAt(r.rack.size()-1) instanceof RemoteControl){
               q = r.rack.pop();
               q.eject();
            }

            else if(r.rack.size() > 1 && r.rack.elementAt(r.rack.size()-2) instanceof RemoteControl && (((RemoteControl) r.rack.elementAt(r.rack.size()-2))).getEjected()){
               q = r.rack.pop();
            }
            else {
                q = r.rack.pop();
                System.out.println("Popped " + q);
            }
            return false;
        }
    }

    public PowerUp getCurrentPowerup(PowerRack p){
        if(p.rack.empty()){ // only has one rack and its empty
            return p;
        }
        if(p.rack.elementAt(p.rack.size() - 1) instanceof PowerRack){ // if tank has a rack as most recent
            if(((PowerRack) p.rack.elementAt(p.rack.size() - 1)).rack.empty()){ // if that rack is empty, then its the power-up used
                return p.rack.elementAt(p.rack.size() - 1);
            }
            PowerUp pup = getCurrentPowerup((PowerRack) p.rack.elementAt(p.rack.size() - 1)); // otherwise, check the most recent rack's power rack
            return pup;
        }
        else { // otherwise, tank has a real powerup as most recent
            return p.rack.elementAt(p.rack.size() - 1);
        }
    }
}
