package net.zacharybest.supercrazytiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static android.R.attr.configure;
import static android.R.attr.width;

/**
 * Provides a means to launch the selected game board dimensions
 */

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
    }

    public void start4x4Game(View view) {
        Intent intent = new Intent(this, GameBoard.class);
        Bundle bundle = new Bundle();
        bundle.putInt("boardId", R.layout.activity_game_board);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
