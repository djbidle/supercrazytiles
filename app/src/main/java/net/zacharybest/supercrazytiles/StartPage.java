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
        Bundle bundle = createBundle(R.layout.activity_game_board, 4, 4);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void start4x5Game(View view) {
        Intent intent = new Intent(this, GameBoard.class);
        Bundle bundle = createBundle(R.layout.activity_game_board_4x5, 4, 5);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void start5x5Game(View view) {
        Intent intent = new Intent(this, GameBoard.class);
        Bundle bundle = createBundle(R.layout.activity_game_board_5x5, 5, 5);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private Bundle createBundle(int boardId, int width, int height){
        Bundle bundle = new Bundle();
        bundle.putInt("boardId", boardId);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        return bundle;
    }
}
