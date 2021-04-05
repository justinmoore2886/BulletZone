package edu.unh.cs.cs619.bulletzone.Controls;
import android.content.Context;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import androidx.test.core.app.ApplicationProvider;
import edu.unh.cs.cs619.bulletzone.Controls.Controller;
import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Singletons.Bus;
import edu.unh.cs.cs619.bulletzone.Singletons.UserTank;

//@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
@RunWith(RobolectricTestRunner.class)
public class ControllerTest {

    private long id;
    private String url;
    private Controller mController;
    private UserTank mUserTank;

    private final Context context = ApplicationProvider.getApplicationContext();
    private int[] buttons;

    @Before
    public void setup(){
        this.id = 0;
        this.url = "http://stman1.cs.unh.edu:6192/games";
        this.mController = Mockito.spy(new Controller(url));
        this.mUserTank = UserTank.getInstance(0);
        this.buttons = new int[] {
                R.id.buttonForward,
                R.id.buttonBack,
                R.id.buttonRight,
                R.id.buttonLeft,
                R.id.buttonLeave,
                R.id.forwardFire,
                R.id.buttonEject
        };
    }

    @Test
    public void sendAction_MoveForwards_ReturnTrue() {
        for (int i = 0; i < 8; i+=2) {
            mUserTank.setDirection(i);
            mController.buttonAction(buttons[0],context);
            verify(mController).sendAction(url + "/" + String.valueOf(id) + "/move/" + String.valueOf(i));
        }
    }

    @Test
    public void sendAction_MoveBackwards_ReturnTrue() {
        for (int i = 0; i < 8; i+=2) {
            mUserTank.setDirection(i);
            mController.buttonAction(buttons[1],context);
            verify(mController).sendAction(url + "/" + String.valueOf(id) + "/move/" + String.valueOf((i+ 4) %8));
        }
    }

    @Test
    public void sendAction_MoveRight_ReturnTrue() {
        for (int i = 0; i < 8; i+=2) {
            mUserTank.setDirection(i);
            mController.buttonAction(buttons[2],context);
            verify(mController).sendAction(url + "/" + String.valueOf(id) + "/turn/" + String.valueOf((i+ 2) % 8));
        }
    }

    @Test
    public void sendAction_MoveLeft_ReturnTrue() {
        int j = 0;
        for (int i = 0; i < 8; i+=2) {
            mUserTank.setDirection(i);
            mController.buttonAction(buttons[3],context);

            if(i == 0){j = 6;}
            else if(i == 2){j = 0;}
            else if(i == 4){j = 2;}
            else if(i == 6){j = 4;}
            
            verify(mController).sendAction(url + "/" + String.valueOf(id) + "/turn/" + String.valueOf(j));
        }
    }

    @Test
    public void sendAction_FireBullet_ReturnTrue() {
        Controller mController = Mockito.spy(new Controller(url));
        mController.buttonAction(buttons[5],context);
        verify(mController).sendAction(url + "/" + String.valueOf(id) + "/fire/" + String.valueOf(1));
    }

    @Test
    public void leaveGame_UserExits_ReturnTrue() {
        Controller mController = Mockito.spy(new Controller(url));
        mController.buttonAction(buttons[4],context);
        verify(mController).leaveGame(url + "/" + String.valueOf(id) + "/leave",context);
    }

    @Test
    public void eject_ejectFromTank_ReturnTrue() {
        Controller mController = Mockito.spy(new Controller(url));
        mController.buttonAction(buttons[6],context);
        verify(mController).sendAction(url + "/" + String.valueOf(id) + "/eject/" + String.valueOf(1));
    }
}