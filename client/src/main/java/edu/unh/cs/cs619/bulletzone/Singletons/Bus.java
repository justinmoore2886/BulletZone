package edu.unh.cs.cs619.bulletzone.Singletons;
import org.greenrobot.eventbus.EventBus;

/**
 * <h1> Bus Class! </h1>
 * The Bus Class creates a greenrobot EventBus.
 * @author ryancontois
 * @version 1.0
 * @since 10/31/2018
 */
public class Bus {
    private static Bus _instance = null;
    public EventBus mEventBus;

    /**
     * This method lets Bus be a singleton by reusing a single instance.
     * @return _instance
     */
    public static Bus getInstance() {
        if (_instance == null)
            _instance = new Bus();
        return _instance;
    }

    /**
     * This is the constructor. When you construct a Bus, you also construct an EventBus.
     */
    public Bus() {
        mEventBus = EventBus.getDefault();
    }

    public void setInstanceToNull() {
        _instance = null;
    }
}
