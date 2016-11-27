package net.zacharybest.supercrazytiles;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;


/**
 * This activity is the game.
 */
public class GameBoard extends Activity {

    private ArrayList<ToggleButton> computerButtons;
    private ArrayList<ToggleButton> playerButtons;
    private int boardWidth;

    private static GameStats stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets view with no title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Sets view to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle bundle = getIntent().getExtras();
        setContentView(bundle.getInt("boardId"));

        boardWidth = getBoardWidth();

        computerButtons = new ArrayList<ToggleButton>();
        addBoardToArray(computerButtons, "computer_board");

        playerButtons = new ArrayList<ToggleButton>();
        addBoardToArray(playerButtons, "player_board");
    }

    @Override
    public void onStart(){
        super.onStart();
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("/data/data/net.zacharybest.supercrazytiles/saved_state.bin")
            );
            stats = (GameStats) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (stats == null) {
            stats = new GameStats();
        }
        newGame();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        /* ONLY WHEN LIVE!!!!!!!!
        * DO NOT UNCOMMENT!!!!!
        * DO NOT REMOVE!!!!!!!!!!!
        AdRequest adRequest = new AdRequest.Builder().build();
        */
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("")
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onPause(){
        super.onPause();
        ArrayList<Boolean> computerBoard = populateBooleanArrayList(computerButtons);
        ArrayList<Boolean> playerBoard = populateBooleanArrayList(playerButtons);
        stats.saveGame(computerBoard, playerBoard);
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
        int max = computerButtons.size() - 1;
        int min = 0;
        for (int i = 0; i < stats.getDifficulty(); i++) {
            int rIndex = rng.nextInt(max - min + 1) + min;
            computerButtons.get(rIndex).toggle();
            togglePlus(computerButtons.get(rIndex), computerButtons);
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
                Toast.makeText(this, "You Lose...", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "You Win!!!", Toast.LENGTH_SHORT).show();
        stats.handleWin();
        newGame();
    }

}
