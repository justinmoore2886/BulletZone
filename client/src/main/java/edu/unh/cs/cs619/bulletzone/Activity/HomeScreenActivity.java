package edu.unh.cs.cs619.bulletzone.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import edu.unh.cs.cs619.bulletzone.Activity.ClientActivity_;
import edu.unh.cs.cs619.bulletzone.R;

public class HomeScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Join the current game as a tank */
        ImageView joinTank = findViewById(R.id.tankView);
        joinTank.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, ClientActivity_.class);
                intent = addToIntent(intent, 1);
                startActivity(intent);
            }
        });

        /* Join the current game as a ship */
        ImageView joinShip = findViewById(R.id.shipView);
        joinShip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something
                Intent intent = new Intent(HomeScreenActivity.this, ClientActivity_.class);
                intent = addToIntent(intent, 4);
                startActivity(intent);
            }
        });

        /* Replay the last played game */
        Button replayGame = findViewById(R.id.replayButton);
        replayGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, ReplayActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This method defines what will be called on app start up.
     * It sets content view and initializes everything that is needed to play the game.
     * @param intent This is for storing the intent
     * @param identifier Identifier of tank/soldier/etc
     */
    protected Intent addToIntent(Intent intent, int identifier){
        return intent.putExtra("identifier", identifier);
    }
}
