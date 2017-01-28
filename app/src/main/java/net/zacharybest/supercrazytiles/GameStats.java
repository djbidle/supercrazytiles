package net.zacharybest.supercrazytiles;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class tracks all of the important game metrics
 */

class GameStats implements Serializable, Parcelable{


    protected int difficulty = 1;     //the difficulty rating of the computer puzzle
    private int lives = 3;          //the number of failed attempts allowed
    protected int turns = difficulty; //the exact number of turns the user must take to complete a level
    private int winIndex = 0;       //tracks number of wins needed for increasing difficulty
    private int score = 0;          //the player's score

    private ArrayList<Boolean> computerBoard;
    private ArrayList<Boolean> playerBoard;

    public void saveGame(ArrayList<Boolean> computerBoard, ArrayList<Boolean> playerBoard, Context context){
        this.computerBoard = computerBoard;
        this.playerBoard = playerBoard;

        String fileLocation = context.getFilesDir().getPath() + context.getString(R.string.save_file);
        Log.d("SAVING_FILE", fileLocation);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(
                            new File(fileLocation)
                    )
            );
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int handleWin(){
        this.winIndex++;
        int scoreToAdd = this.difficulty * 100;
        addScore(scoreToAdd);
        int winThreshold = 3;
        if (winIndex % winThreshold == 0 && difficulty < 9){
            difficulty++;
            winIndex = 0;
        }
        turns = difficulty;
        return scoreToAdd;
    }

    public void handleLoss(){
        this.lives--;
        turns = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getLives() {
        return lives;
    }

    public String getLivesString() { return String.valueOf(lives); }

    public int getTurns() {
        return turns;
    }

    public void useTurn(){
        this.turns--;
    }

    public int getScore() {
        return score;
    }

    private void addScore(int score){
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this);
    }

    public static final Parcelable.Creator<GameStats> CREATOR = new Parcelable.Creator<GameStats>() {
        public GameStats createFromParcel(Parcel in) {
            return (GameStats) in.readSerializable();
        }

        public GameStats[] newArray(int size) {
            return new GameStats[size];
        }
    };
}
