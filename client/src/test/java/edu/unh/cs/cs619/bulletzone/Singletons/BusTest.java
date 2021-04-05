package edu.unh.cs.cs619.bulletzone.Singletons;

import org.junit.Test;
import org.junit.Assert;

import edu.unh.cs.cs619.bulletzone.Singletons.Bus;

public class BusTest {
    @Test
    public void getInstance_TwoCopiesOfInstance_EnsureEqual() {
        Bus testBus = Bus.getInstance();

        Assert.assertNotNull(testBus);

        Bus testBusTwo = Bus.getInstance();

        Assert.assertSame(testBus,testBusTwo);
    }
}