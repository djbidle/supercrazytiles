package net.zacharybest.supercrazytiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


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
        startGame(new GameStats());
    }

    public void newUnlimitedGame(View view){
        save.delete();
        startGame(new UnlimitedGameStats());
    }

    //onclick action
    @SuppressWarnings("WeakerAccess")
    public void continueGame(@SuppressWarnings("UnusedParameters") View view){
        try {
            GameStats gameStats = loadSave();
            startGame(gameStats);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openTutorial(View view){
        Intent intent = new Intent(this, DisplayTutorialActivity.class);
        startActivity(intent);
    }

    private void startGame(GameStats gameStats){
        Intent intent = new Intent(this, GameBoard.class);
        intent.putExtra("game_stats", gameStats);
        Bundle bundle = new Bundle();
        bundle.putInt("boardId", R.layout.activity_game_board);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private GameStats loadSave() throws IOException, ClassNotFoundException{
        String fileLocation = getFilesDir().getPath() + getString(R.string.save_file);
        Log.d("LOADING_SAVE_FILE", fileLocation);
        ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(fileLocation)
        );
        return (GameStats) ois.readObject();
    }
}
