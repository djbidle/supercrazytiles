package net.zacharybest.supercrazytiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by dj on 10/29/16.
 */

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, GameBoard.class);
        Bundle bundle = new Bundle();

        if (view.getId() == R.id.start_game_4x4) {
            bundle.putInt("Key", view.getId());
        }

        if (view.getId() == R.id.start_game_4x5) {
            bundle.putInt("Key", view.getId());
        }

        if (view.getId() == R.id.start_game_5x5) {
            bundle.putInt("Key", view.getId());
        }

        intent.putExtras(bundle);
        startActivity(intent);

    }

}
