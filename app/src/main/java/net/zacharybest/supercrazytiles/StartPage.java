package net.zacharybest.supercrazytiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9912906347455873~4687548548");

        save = new File(getFilesDir().getPath() + getString(R.string.save_file));
        if (save.exists()){
            Button btn = (Button) findViewById(R.id.continueButton);
            btn.setClickable(true);
            btn.setEnabled(true);
        }

        loadBannerAd();
    }

    private void loadBannerAd(){
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void newGame(View view) {
        //noinspection ResultOfMethodCallIgnored
        save.delete();
        continueGame(view);
    }

    //onclick action
    @SuppressWarnings("WeakerAccess")
    public void continueGame(@SuppressWarnings("UnusedParameters") View view){
        Intent intent = new Intent(this, GameBoard.class);
        Bundle bundle = new Bundle();
        bundle.putInt("boardId", R.layout.activity_game_board);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
