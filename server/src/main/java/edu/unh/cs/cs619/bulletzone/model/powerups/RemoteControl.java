package edu.unh.cs.cs619.bulletzone.model.powerups;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public class RemoteControl extends PowerUp  {
    Tank owner;
   Tank controller;
   boolean ejected = false;

   public boolean getEjected(){return ejected;}

    public void setEjected(){ejected = true;}

    public void setOwner(Tank Owner){owner = Owner ;}

    public RemoteControl(int pos){
        setPos(pos);
    }

    @Override
    public FieldEntity copy() {
        RemoteControl temp = new RemoteControl(getPos());
        temp.owner = this.owner;
        temp.controller = this.controller;
        temp.ejected = this.ejected;
       return temp;
    }

    @Override
    public int getIntValue() {
        return 4600004;
    }

    @Override
    public String toString() {
        return "RC";
    }

    /**
     * This method applies the power ups effect to the user
     * @param vehicle The users vehicle
     */
    public void applyEffect(Tank vehicle){
        System.out.println("applly effect vehicle id" + vehicle.getId() + " vehicle owner " + vehicle.getOwnerId()+"\n");
        System.out.println("applly effect controller id" + controller.getId() + " controller owner " + controller.getOwnerId()+"\n");
        if(vehicle.getId() == vehicle.getOwnerId() && !ejected) {
            owner = vehicle;
            vehicle.setOwnerId(vehicle.getId());
            vehicle.setId(controller.getId() * 10);
            vehicle.setControllerId(controller.getId());
            controller.setControls(vehicle.getOwnerId());
        }
    }

    public void setEffect(Tank controllerTank){
        controller = controllerTank;
    }

    /**
     * This method remove remote control status
     */
    public void eject(){
        controller.setControls(-1);
        owner.setControllerId(-1);
        ejected = true;
        System.out.println("eject owner owner id" + owner.getOwnerId() + " owner id " + owner.getId()+"\n");
        if(owner.getOwnerId() > -1){
            owner.setId(owner.getOwnerId());
        }
        else{
            owner.setId(100);
            owner.clearBullets();
            if(owner.getIdentifier() == 1) {
                owner.setIdentifier(3);
            }
            else{owner.setIdentifier(5);}
        }
    }
}
