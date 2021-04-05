package edu.unh.cs.cs619.bulletzone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import edu.unh.cs.cs619.bulletzone.Controls.ControllerTest;
import edu.unh.cs.cs619.bulletzone.Grid.DatabaseTest;
import edu.unh.cs.cs619.bulletzone.Grid.GridAdapterTest;

@RunWith(RobolectricTestRunner.class)
public class _AllClientTests {
    ControllerTest clientControllerTests = new ControllerTest();
    GridAdapterTest clientGridAdapterTests = new GridAdapterTest();
    DatabaseTest clientDBHelperTests = new DatabaseTest();

    @Before
    public void setup(){
        clientControllerTests.setup();
    }

    @Test
    public void Milestone_1_UntimedActionsFromUIFormulateCorrectCalls() throws Exception{
        clientControllerTests.sendAction_MoveForwards_ReturnTrue();
        clientControllerTests.setup();
        clientControllerTests.sendAction_MoveBackwards_ReturnTrue();
        clientControllerTests.setup();
        clientControllerTests.sendAction_MoveRight_ReturnTrue();
        clientControllerTests.setup();
        clientControllerTests.sendAction_MoveLeft_ReturnTrue();
        clientControllerTests.setup();
        clientControllerTests.sendAction_FireBullet_ReturnTrue();
        clientControllerTests.setup();
        clientControllerTests.leaveGame_UserExits_ReturnTrue();
        clientControllerTests.setup();
        clientControllerTests.eject_ejectFromTank_ReturnTrue();
        clientControllerTests.setup();
    }

    @Test
    public void Milestone_1_UpdateToGameBoardStructure() throws Exception{
        clientGridAdapterTests.setUp();
        clientGridAdapterTests.getCount_FullGridSize_Returns256();
        clientGridAdapterTests.getItemId_FullGridOfCellsWithId_Return13();
        clientGridAdapterTests.addGrid_FullSimulationGridAdded_EnsureAdded();
    }

    @Test
    public void Milestone_2_DatabaseUpdateTest() throws Exception{
        clientDBHelperTests.isDatabaseEmpty_DatabaseEmpty_ReturnTrue();
        clientDBHelperTests.onCreate_CreateNewDatabase_ReturnTrue();
        clientDBHelperTests.addData_InsertsIdAnGrid_ReturnTrue();
        clientDBHelperTests.getDatabaseData_RetrieveThreeRows_ReturnTrue();
    }

}
