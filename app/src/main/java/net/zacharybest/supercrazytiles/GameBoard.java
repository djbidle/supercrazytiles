package net.zacharybest.supercrazytiles;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Random;

import static net.zacharybest.supercrazytiles.R.id.diffLevel;

/**
 * This activity is the game.
 */
public class GameBoard extends Activity {

    private ArrayList<Button> computerButtons;
    private ArrayList<Button> playerButtons;
    private int boardWidth;
    private int boardHeight;
    private int difficulty = 0;
    private int turn = difficulty;

    int activeColor = 0xFF388E3C; //green
    int inactiveColor = 0xFF1A237E; //blue
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets view with no title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Sets view to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("Key");

        if (id == R.id.start_game_4x4) {
            setContentView(R.layout.activity_game_board);
            boardHeight = 4;
        }

        if (id == R.id.start_game_4x5) {
            setContentView(R.layout.activity_game_board_4x5);
            boardHeight = 5;
        }

        if (id == R.id.start_game_5x5) {
            setContentView(R.layout.activity_game_board_5x5);
            boardHeight = 5;
        }

        computerButtons = new ArrayList<Button>();
        addBoardToArray(computerButtons, "computer_board");

        playerButtons = new ArrayList<Button>();
        addBoardToArray(playerButtons, "player_board");

        //FIXME sqrt will not work with rectangles
        boardWidth = (int) Math.sqrt((double) playerButtons.size());

        /**
         * The only way to set button size dynamically with
         * GridLayout is to use columnWeight/rowWeight attributes.
         * TableLayout didn't work because addBoardToArray was not
         * getting all the buttons. Did not want to mess with that code.
         **/
        setButtonSize(computerButtons);
        setButtonSize(playerButtons);

        newGame();

    }

    /**
     * starts a new game
     */
    private void newGame() {
        resetBoard(computerButtons);
        resetBoard(playerButtons);
        increaseDifficult();
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

    /**
     * Increase the difficulty and notify the user
     */
    private void increaseDifficult() {
        difficulty++;
        turn = difficulty;

        TextView tv = (TextView) findViewById(diffLevel);
        tv.setText(String.valueOf(difficulty));
        //Toast.makeText(this, "Difficulty set to " + difficulty, Toast.LENGTH_SHORT).show();
    }

    /**
     * get all buttons from container and add to array
     *
     * @param array   receives buttons
     * @param boardId the id of the container to grab buttons from
     */
    private void addBoardToArray(ArrayList<Button> array, String boardId) {
        int id = getResources().getIdentifier(boardId, "id", getPackageName());
        View board = (View) findViewById(id);
        for (int index = 0; index < ((ViewGroup) board).getChildCount(); ++index) {
            View nextChild = ((ViewGroup) board).getChildAt(index);
            if (nextChild instanceof Button) {
                Button button = (Button) nextChild;
                array.add(button);
            }
        }
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
            if (turn == 0) {
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

    private void setButtonSize(ArrayList<Button> buttons) {
        for (Button button : buttons) {
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) button.getLayoutParams();
            params.width = (getResources().getDisplayMetrics().widthPixels / (boardWidth + 1));
            params.height = (getResources().getDisplayMetrics().heightPixels / ((boardHeight * 2) + 2));
            button.setLayoutParams(params);
        }

    }

}
