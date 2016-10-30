package net.zacharybest.supercrazytiles;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * This activity is the game.
 */
public class GameBoard extends AppCompatActivity {

    private ArrayList<Button> computerButtons;
    private ArrayList<Button> playerButtons;
    private int boardRowLength;
    private int difficulty = 0;
    private int turn = difficulty;

    int activeColor = 0xFF00FF00; //green
    int inactiveColor = 0xFF0000FF; //blue

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        computerButtons = new ArrayList<Button>();
        addBoardToArray(computerButtons, "computer_board");

        playerButtons = new ArrayList<Button>();
        addBoardToArray(playerButtons, "player_board");

        boardRowLength = (int) Math.sqrt((double) playerButtons.size());

        newGame();
    }

    /**
     * starts a new game
     */
    private void newGame(){
        resetBoard(computerButtons);
        resetBoard(playerButtons);
        increaseDifficult();
        setComputerBoard();
    }

    /**
     * set all tiles to their inactive state
     * @param board the collection of tiles to be reset
     */
    private void resetBoard(ArrayList<Button> board){
        for(Button button : board){
            button.setTextColor(inactiveColor);
            button.setBackgroundColor(inactiveColor);
        }
    }

    /**
     * Increase the difficulty and notify the user
     */
    private void increaseDifficult(){
        difficulty++;
        turn = difficulty;
        Toast.makeText(this, "Difficulty set to " + difficulty,Toast.LENGTH_SHORT).show();
    }

    /**
     * get all buttons from container and add to array
     * @param array receives buttons
     * @param boardId the id of the container to grab buttons from
     */
    private void addBoardToArray(ArrayList<Button> array, String boardId){
        int id = getResources().getIdentifier(boardId, "id", getPackageName());
        View board = (View) findViewById(id);
        for(int index = 0; index<((ViewGroup) board).getChildCount(); ++index) {
            View nextChild = ((ViewGroup) board).getChildAt(index);
            if (nextChild instanceof Button){
                Button button = (Button) nextChild;
                array.add(button);
            }
        }
    }

    /**
     * randomly selects tiles to activate on the computer board.
     * The number of tiles selected depends on the "difficulty" int
     */
    private void setComputerBoard(){
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
     * @param view the button the player activated to spend their turn
     */
    public void takeTurn(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            ArrayList<Button> board = playerButtons.contains(button) ? playerButtons : computerButtons;
            togglePlus(button, board);

            turn--;
            if(turn == 0){
                checkForWin();
            }
        }
    }

    /**
     * Toggles the specified button and the buttons adjacent to in a plus "+" pattern
     * @param button the center of the "+"
     * @param board the board on which the button was clicked
     */
    private void togglePlus(Button button, ArrayList<Button> board){
        int buttonIndex = board.indexOf(button);
        int leftIndex = buttonIndex - 1;
        int rightIndex = buttonIndex + 1;
        int topIndex = buttonIndex - boardRowLength;
        int bottomIndex = buttonIndex + boardRowLength;

        toggleButton(button);

        // if a button exists to the left of the button that was clicked
        if (buttonIndex % boardRowLength > 0) {
            toggleButton(board.get(leftIndex));
        }
        // if a button exists to the right of the button that was clicked
        if ((buttonIndex % boardRowLength) < boardRowLength - 1) {
            toggleButton(board.get(rightIndex));
        }
        // if a button exists above the button that was clicked
        if (topIndex >= 0) {
            toggleButton(board.get(topIndex));
        }
        // if a button exists below the button that was clicked
        if (bottomIndex < board.size()) {
            toggleButton(board.get(bottomIndex));
        }
    }

    /**
     * Checks to see if the player won or lost.
     * Notifies player of the result
     */
    private void checkForWin(){
        for (int i = 0; i < playerButtons.size(); i++){
            int playerButtonColor = playerButtons.get(i).getCurrentTextColor();
            int computerButtonColor = computerButtons.get(i).getCurrentTextColor();
            if (playerButtonColor != computerButtonColor){
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
     * @param button the target of the color change
     */
    private void toggleButton(Button button){
        int currentColor = button.getCurrentTextColor();
        int color = currentColor == activeColor ? inactiveColor : activeColor;
        button.setTextColor(color);
        button.setBackgroundColor(color);
    }


}
