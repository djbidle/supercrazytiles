package net.zacharybest.supercrazytiles;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Random;


/**
 * This activity is the game.
 */
public class GameBoard extends Activity {

    private ArrayList<Button> computerButtons;
    private ArrayList<Button> playerButtons;
    private int boardWidth;
    private int difficulty = 0;
    private int turn = difficulty;

    private final int activeColor = 0xFF388E3C; //green
    private final int inactiveColor = 0xFF1A237E; //blue

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

        computerButtons = new ArrayList<Button>();
        addBoardToArray(computerButtons, "computer_board");

        playerButtons = new ArrayList<Button>();
        addBoardToArray(playerButtons, "player_board");

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

    /**
     * starts a new game
     */
    private void newGame() {
        resetBoard(computerButtons);
        resetBoard(playerButtons);
        increaseDifficulty();
        updateTurnView();
        setComputerBoard();
    }

    /**
     * set all tiles to their inactive state
     *
     * @param board the collection of tiles to be reset
     */
    private void resetBoard(ArrayList<Button> board) {
        for (Button button : board) {
            button.setTextColor(inactiveColor);
            button.setBackgroundColor(inactiveColor);
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
    private void addBoardToArray(ArrayList<Button> array, String boardId) {
        int id = getResources().getIdentifier(boardId, "id", getPackageName());
        View board = findViewById(id);
        for (int index = 0; index < ((ViewGroup) board).getChildCount(); ++index) {
            View row = ((ViewGroup) board).getChildAt(index);

            for(int i = 0; i < ((ViewGroup) row).getChildCount(); i++) {
               View nextChild = ((ViewGroup) row).getChildAt(i);
                if (nextChild instanceof Button) {
                    Button button = (Button) nextChild;
                    array.add(button);
                }
            }
        }
    }

    /**
     * Increase the difficulty and notify the user
     */
    private void increaseDifficulty() {
        difficulty++;
        turn = difficulty;

        TextView tv = (TextView) findViewById(R.id.diffLevel);
        tv.setText(String.valueOf(difficulty));
    }

    /**
     * randomly selects tiles to activate on the computer board.
     * The number of tiles selected depends on the "difficulty" int
     */
    private void setComputerBoard() {
        Random rng = new Random();
        int max = computerButtons.size() - 1;
        int min = 0;
        for (int i = 0; i < difficulty; i++) {
            int rIndex = rng.nextInt(max - min + 1) + min;
            togglePlus(computerButtons.get(rIndex), computerButtons);
        }
    }

    /**
     * consumes the players turn
     *
     * @param view the button the player activated to spend their turn
     */
    public void takeTurn(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            ArrayList<Button> board = playerButtons.contains(button) ? playerButtons : computerButtons;
            togglePlus(button, board);

            turn--;
            updateTurnView();
            if (turn == 0) {
                checkForWin();
            }
        }
    }

    /**
     * Updates the GUI with the number of turns remaining
     */
    private void updateTurnView(){
        TextView turnsLeft = (TextView) findViewById(R.id.movesLeft);
        turnsLeft.setText(String.valueOf(turn));
    }

    /**
     * Toggles the specified button and the buttons adjacent to in a plus "+" pattern
     *
     * @param button the center of the "+"
     * @param board  the board on which the button was clicked
     */
    private void togglePlus(Button button, ArrayList<Button> board) {
        int buttonIndex = board.indexOf(button);
        int leftIndex = buttonIndex - 1;
        int rightIndex = buttonIndex + 1;
        int topIndex = buttonIndex - boardWidth;
        int bottomIndex = buttonIndex + boardWidth;

        toggleButton(button);
        if (buttonIndex % boardWidth > 0) {
            toggleButton(board.get(leftIndex));
        }
        if ((buttonIndex % boardWidth) < boardWidth - 1) {
            toggleButton(board.get(rightIndex));
        }
        if (topIndex >= 0) {
            toggleButton(board.get(topIndex));
        }
        if (bottomIndex < board.size()) {
            toggleButton(board.get(bottomIndex));
        }
    }

    /**
     * Checks to see if the player won or lost.
     * Notifies player of the result
     */
    private void checkForWin() {
        for (int i = 0; i < playerButtons.size(); i++) {
            int playerButtonColor = playerButtons.get(i).getCurrentTextColor();
            int computerButtonColor = computerButtons.get(i).getCurrentTextColor();
            if (playerButtonColor != computerButtonColor) {
                Toast.makeText(this, "You Lose...", Toast.LENGTH_SHORT).show();
                difficulty = 0;
                newGame();
                return;
            }
        }
        Toast.makeText(this, "You Win!!!", Toast.LENGTH_SHORT).show();
        newGame();
    }

    /**
     * changes the button color to active if inactive
     * changes the button color to inactive if active
     *
     * @param button the target of the color change
     */
    private void toggleButton(Button button) {
        int currentColor = button.getCurrentTextColor();
        int color = currentColor == activeColor ? inactiveColor : activeColor;
        button.setTextColor(color);
        button.setBackgroundColor(color);
    }

}
