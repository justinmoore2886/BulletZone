package edu.unh.cs.cs619.bulletzone.model;

/**
 * <h1> NumberField Class! </h1>
 * This class does server stuff......
 * @author given
 * @version 1.0
 * @since Halloween
 */

public class NumberField extends FieldEntity {

    private static final String TAG = "NumberField";
    private final int value;

    public NumberField(int value) {
        this.value = value;
    }

    @Override
    public int getIntValue() {
        return 0;
    }

    @Override
    public FieldEntity copy() {
        return null;
    }

    @Override
    public String toString() {
        return Integer.toString(value == 1000 ? 1 : 2);
    }

    /**
     * This method applies an action on a vehicle based on coast properties
     * @param vehicle The users vehicle
     */
    @Override
    public boolean action(Tank vehicle){return true;}

}
