package net.zacharybest.supercrazytiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class tracks all of the important game metrics
 */

public class GameStats implements Serializable{


    private int difficulty = 1;     //the difficulty rating of the computer puzzle
    private int lives = 3;          //the number of failed attempts allowed
    private int turns = difficulty; //the exact number of turns the user must take to complete a level
    private int winThreshold = 3;   //number of wins needed to increase difficulty
    private int winIndex = 0;       //tracks number of wins needed for increasing difficulty
    private int score = 0;          //the player's score

    private ArrayList<Boolean> computerBoard;
    private ArrayList<Boolean> playerBoard;


    public void saveGame(ArrayList<Boolean> computerBoard, ArrayList<Boolean> playerBoard){
        this.computerBoard = computerBoard;
        this.playerBoard = playerBoard;

        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(
                            new File("/data/data/net.zacharybest.supercrazytiles/saved_state.bin")
                    )
            );
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addGameOutcome(boolean outcome){
        if (outcome){
            handleWin();
        } else {
            handleLoss();
        }
    }

    public void handleWin(){
        this.winIndex++;
        addScore(this.difficulty * 100);
        if (winIndex % winThreshold == 0){
            difficulty++;
            winIndex = 0;
        }
        turns = difficulty;
    }

    public void handleLoss(){
        this.lives--;
        turns = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getLives() {
        return lives;
    }

    public int getTurns() {
        return turns;
    }

    public void useTurn(){
        this.turns--;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score){
        this.score += score;
    }

    public ArrayList<Boolean> getComputerBoard() {
        return computerBoard;
    }

    public ArrayList<Boolean> getPlayerBoard() {
        return playerBoard;
    }

    public void deleteBoards(){
        computerBoard = null;
        playerBoard = null;
    }
}
