package edu.unh.cs.cs619.bulletzone.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.unh.cs.cs619.bulletzone.Singletons.Bus;
import edu.unh.cs.cs619.bulletzone.Grid.DatabaseRetriever;
import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.Grid.SimGridFacade;

/**
 * <h1> Replay Activity Class! </h1>
 * The replay activity class initializes everything that is needed on the replay activity when
 * the game starts.
 *
 * @author Justin Moore
 * @version 1.0
 * @since 11/12/2018
 */
public class ReplayActivity extends Activity {

    private DatabaseRetriever databaseRetriever;
    private RadioGroup radioGroup;
    private Button replayButton;
    SimGridFacade myFacade;
    Bus eventBus;
    String selectedSpeed = "100";

    /**
     * This method defines what will be called on app start up.
     * It sets content view and initializes everything that is needed to play the game.
     * @param savedInstanceState This is for proper phone rotations.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);

        // Various buttons and views
        replayButton = findViewById(R.id.replayButton);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(R.id.normalSpeed);
        GridView gridView = findViewById(R.id.replayGrid);

        // For polling
        eventBus = Bus.getInstance();
        myFacade = new SimGridFacade(this);
        myFacade.setAdapter(gridView);
        databaseRetriever = new DatabaseRetriever(myFacade, this, this, replayButton, radioGroup);

        // No games played yet
        if(databaseRetriever.isDatabaseEmpty()) {
            setReplayDisabled();
        }

        /* Replay game */
        replayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Initial start of replay
                if(replayButton.getText().equals("Playback last game")) {
                    replayButton.setText(R.string.pause);

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedId);
                    selectedSpeed = radioButton.getText().toString();

                    databaseRetriever.startPolling(selectedSpeed);

                    for(int i = 0; i < radioGroup.getChildCount(); i++){
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                }
                // User pauses game
                else if(replayButton.getText().equals("Pause")) {
                    replayButton.setText(R.string.resume);
                    databaseRetriever.setPollUnavailable();
                }
                // User resumes game
                else if(replayButton.getText().equals("Resume")) {
                    replayButton.setText(R.string.pause);
                    databaseRetriever.setPollAvailable(selectedSpeed);
                }
            }
        });

        /* Leave replay mode */
        Button leaveReplay = findViewById(R.id.leaveReplay);
        leaveReplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ReplayActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This disables replay buttons
     */
    private void setReplayDisabled() {
        Toast toast = Toast.makeText(getApplicationContext(), "No games played yet", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 500);

        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(38);

        toast.show();

        for(int i = 0; i < radioGroup.getChildCount(); i++){
            radioGroup.getChildAt(i).setEnabled(false);
        }
        replayButton.setEnabled(false);
    }
}