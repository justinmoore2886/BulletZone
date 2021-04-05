package edu.unh.cs.cs619.bulletzone.Activity;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import androidx.test.core.app.ApplicationProvider;
import edu.unh.cs.cs619.bulletzone.Activity.ClientActivity;
import edu.unh.cs.cs619.bulletzone.Grid.SimGridFacade;
import edu.unh.cs.cs619.bulletzone.R;

@RunWith(RobolectricTestRunner.class)
public class ClientActivityTest {




    private final Context context = ApplicationProvider.getApplicationContext();
    private SimGridFacade simGridFacade = new SimGridFacade(context);

    private ActivityController clientActivity = Robolectric.buildActivity(ClientActivity.class);




    @Before
    public void onCreate_CreateGridView_EnsureEqual() throws Exception {
        clientActivity.create().start().resume().visible().get();
    }

    @Test
    public void t01_testOne() throws Exception {
        //clientActivity.initialize();
    }
}
