package net.zacharybest.supercrazytiles;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;


/**
 * This activity is the game.
 */
public class GameBoard extends Activity {

    private InterstitialAd mInterstitialAd;
    private ArrayList<ToggleButton> computerButtons;
    private ArrayList<ToggleButton> playerButtons;
    private int boardWidth;

    private TextView scoreView;
    private TextView scoreAdd;

    private static GameStats stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        setContentView(bundle.getInt("boardId"));

        scoreView = (TextView) findViewById(R.id.score);
        scoreAdd = (TextView) findViewById(R.id.addScoreDisplay);

        boardWidth = getBoardWidth();

        computerButtons = new ArrayList<ToggleButton>();
        addBoardToArray(computerButtons, "computer_board");

        playerButtons = new ArrayList<ToggleButton>();
        addBoardToArray(playerButtons, "player_board");

    }

    @Override
    public void onStart(){
        super.onStart();
        requestAd();

        try {
            stats = loadSave();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (stats == null) {
            stats = new GameStats();
        }
        newGame();
    }

    private GameStats loadSave() throws IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(getFilesDir().getPath() + getString(R.string.save_file))
        );
        return (GameStats) ois.readObject();
    }

    private void requestAd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded(){
                mInterstitialAd.show();
            }
        });

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onPause(){
        super.onPause();
        ArrayList<Boolean> computerBoard = populateBooleanArrayList(computerButtons);
        ArrayList<Boolean> playerBoard = populateBooleanArrayList(playerButtons);
        stats.saveGame(computerBoard, playerBoard, this);
    }

    private ArrayList<Boolean> populateBooleanArrayList(ArrayList<ToggleButton> arrayList){
        ArrayList<Boolean> output = new ArrayList<Boolean>();
        for (ToggleButton btn : arrayList){
            output.add(btn.isChecked());
        }
        return output;
    }

    /**
     * starts a new game
     */
    private void newGame() {
        resetBoard(computerButtons);
        resetBoard(playerButtons);
        updateStats();
        if (stats.getComputerBoard() != null){
            populateBoards();
            stats.deleteBoards();
        } else {
            setComputerBoard();
        }
    }

    private void populateBoards(){
        for (int i = 0; i < computerButtons.size(); i++){
            computerButtons.get(i).setChecked(stats.getComputerBoard().get(i));
            playerButtons.get(i).setChecked(stats.getPlayerBoard().get(i));
        }
    }

    private void tryAgain(){
        resetBoard(playerButtons);
        updateTurns();
        updateLives();
    }

    /**
     * set all tiles to their inactive state
     *
     * @param board the collection of tiles to be reset
     */
    private void resetBoard(ArrayList<ToggleButton> board) {
        for (ToggleButton button : board) {
            button.setChecked(false);
        }
    }

    private int getBoardWidth(){
        int id = getResources().getIdentifier("computer_board", "id", getPackageName());
        View board = findViewById(id);
        View firstRow = ((ViewGroup) board).getChildAt(0);
        return ((ViewGroup) firstRow).getChildCount();
    }



    /**
     * get all buttons from container and add to array
     *
     * @param array   receives buttons
     * @param boardId the id of the container to grab buttons from
     */
    private void addBoardToArray(ArrayList<ToggleButton> array, String boardId) {
        int id = getResources().getIdentifier(boardId, "id", getPackageName());
        View board = findViewById(id);

        for (int index = 0; index < ((ViewGroup) board).getChildCount(); ++index) {
            View row = ((ViewGroup) board).getChildAt(index);

            for(int i = 0; i < ((ViewGroup) row).getChildCount(); i++) {
               View nextChild = ((ViewGroup) row).getChildAt(i);
                if (nextChild instanceof ToggleButton) {
                    ToggleButton button = (ToggleButton) nextChild;
                    array.add(button);
                }
            }
        }
    }

    /**
     * updates a GUI component with the current stat value
     * @param id the R.id of the resource to be updated.
     */
    private void updateStat(int id, int value){
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.valueOf(value));
    }

    /**
     * updates all GUI components with their current values
     */
    private void updateStats(){
        updateScore();
        updateLives();
        updateDifficulty();
        updateTurns();
    }

    private void updateScore(){
        updateStat(R.id.score, stats.getScore());
    }

    private void updateLives(){
        updateStat(R.id.lives, stats.getLives());
    }

    private void updateDifficulty(){
        updateStat(R.id.diffLevel, stats.getDifficulty());
    }

    private void updateTurns(){
        updateStat(R.id.movesLeft, stats.getTurns());
    }


    /**
     * randomly selects tiles to activate on the computer board.
     * The number of tiles selected depends on the "difficulty" int
     */
    private void setComputerBoard() {
        Random rng = new Random();
        ArrayList<ToggleButton> clonedButtons = (ArrayList<ToggleButton>) computerButtons.clone();
        int min = 0;
        for (int i = 0; i < stats.getDifficulty(); i++) {
            int max = clonedButtons.size() - 1;
            int rIndex = rng.nextInt(max - min + 1) + min;
            ToggleButton selectedButton = clonedButtons.get(rIndex);
            clonedButtons.remove(selectedButton);
            selectedButton.toggle();
            togglePlus(selectedButton, computerButtons);
        }
    }

    /**
     * consumes the players turn
     *
     * @param view the button the player activated to spend their turn
     */
    public void takeTurn(View view) {
        if (view instanceof ToggleButton) {
            ToggleButton button = (ToggleButton) view;
            ArrayList<ToggleButton> board = playerButtons.contains(button) ? playerButtons : computerButtons;
            togglePlus(button, board);

            stats.useTurn();
            updateTurns();
            if (stats.getTurns() == 0) {
                checkForWin();
            }
        }
    }

    /**
     * Toggles the specified button and the buttons adjacent to in a plus "+" pattern
     *
     * @param button the center of the "+"
     * @param board  the board on which the button was clicked
     */
    private void togglePlus(ToggleButton button, ArrayList<ToggleButton> board) {
        int buttonIndex = board.indexOf(button);
        int leftIndex = buttonIndex - 1;
        int rightIndex = buttonIndex + 1;
        int topIndex = buttonIndex - boardWidth;
        int bottomIndex = buttonIndex + boardWidth;

        if (buttonIndex % boardWidth > 0) {
            board.get(leftIndex).toggle();
        }
        if ((buttonIndex % boardWidth) < boardWidth - 1) {
            board.get(rightIndex).toggle();
        }
        if (topIndex >= 0) {
            board.get(topIndex).toggle();
        }
        if (bottomIndex < board.size()) {
            board.get(bottomIndex).toggle();
        }
    }

    /**
     * Checks to see if the player won or lost.
     * Notifies player of the result
     */
    private void checkForWin() {
        for (int i = 0; i < playerButtons.size(); i++) {
            boolean playerButtonState = playerButtons.get(i).isChecked();
            boolean computerButtonState = computerButtons.get(i).isChecked();
            if (playerButtonState != computerButtonState) {
                //Toast.makeText(this, "You Lose...", Toast.LENGTH_SHORT).show();
                stats.handleLoss();
                if ( stats.getLives() > 0) {
                    tryAgain();
                } else {
                    stats = new GameStats();
                    newGame();
                }
                return;
            }
        }

        int currentScore = stats.getScore();
        animateTextCounter(stats.handleWin(), 0, scoreAdd, "+");
        animateTextCounter(currentScore, stats.getScore(), scoreView, "");
        newGame();
    }

    @SuppressLint("Internationalization")
    private void animateTextCounter(int startingValue, int finalValue, final TextView  textView, final String textLead){

        ValueAnimator valueAnimator = ValueAnimator.ofInt(startingValue, finalValue);
        valueAnimator.setDuration(2000);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textView.setText(textLead + valueAnimator.getAnimatedValue().toString());
                if (textView.getText().equals(textLead+"0")){
                    textView.setText("");
                }
            }
        });
        valueAnimator.start();
    }

}
