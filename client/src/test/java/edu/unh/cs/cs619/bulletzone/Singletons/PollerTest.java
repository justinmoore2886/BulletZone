package edu.unh.cs.cs619.bulletzone.Singletons;

import android.content.Context;

import com.android.volley.RequestQueue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;

import androidx.test.core.app.ApplicationProvider;
import edu.unh.cs.cs619.bulletzone.Grid.SimGridFacade;
import edu.unh.cs.cs619.bulletzone.Singletons.Poller;

@RunWith(RobolectricTestRunner.class)
public class PollerTest {

    Context context = ApplicationProvider.getApplicationContext();
    private String url = "http://stman1.cs.unh.edu:61903/games";

    private SimGridFacade simGridFacade;

    @Mock
    private RequestQueue requestQueue;

    @InjectMocks
    private Poller poller;

    @Before
    public void setup(){
        simGridFacade = Mockito.spy(new SimGridFacade());
        poller = new Poller(simGridFacade,context,url);
    }

    @Test
    public void test() throws Exception{
    }
}
