package edu.unh.cs.cs619.bulletzone.model;

import com.google.common.eventbus.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1> FieldEntity Class! </h1>
 * This class serializes the current {@link edu.unh.cs.cs619.bulletzone.model.FieldEntity} instance.
 */
public abstract class FieldEntity {
    //protected static final EventBus eventBus = new EventBus();
    protected FieldHolder parent;
    protected FieldEntity child = null;
    int pos;

    /**
     * Serializes the current {@link edu.unh.cs.cs619.bulletzone.model.FieldEntity} instance.
     *
     * @return Integer representation of the current {@link edu.unh.cs.cs619.bulletzone.model.FieldEntity}
     */
    public int getPos(){
        return pos;
    }

    public void setPos(int p){
        pos = p;
    }

    public abstract int getIntValue();

    public FieldHolder getParent() {
        return parent;
    }

    public void setParent(FieldHolder parent) {
        this.parent = parent;
    }

    public abstract FieldEntity copy();

    public boolean hit(Bullet bullet){
        return true;
    }

    public abstract boolean action(Tank tank);

    public FieldEntity getChild(){return child;}

    public void setChild(FieldEntity child) {
        this.child = child;
    }
}