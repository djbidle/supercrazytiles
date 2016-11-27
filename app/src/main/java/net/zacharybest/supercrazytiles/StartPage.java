package net.zacharybest.supercrazytiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.MobileAds;

import java.io.File;


/**
 * Provides a means to launch the selected game board dimensions
 */

public class StartPage extends AppCompatActivity {

    private File save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        save = new File("/data/data/net.zacharybest.supercrazytiles/saved_state.bin");
        if (save.exists()){
            Button btn = (Button) findViewById(R.id.continueButton);
            btn.setClickable(true);
            btn.setEnabled(true);
        }
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
    }

    public void newGame(View view) {
        save.delete();
        continueGame(view);
    }

    public void continueGame(View view){
        Intent intent = new Intent(this, GameBoard.class);
        Bundle bundle = new Bundle();
        bundle.putInt("boardId", R.layout.activity_game_board);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
