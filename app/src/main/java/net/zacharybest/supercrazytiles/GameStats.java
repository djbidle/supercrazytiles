package net.zacharybest.supercrazytiles;

import static android.R.attr.isGame;

/**
 * This class tracks all of the important game metrics
 */

public class GameStats {

    private int difficulty;     //the difficulty rating of the computer puzzle
    private int lives;          //the number of failed attempts allowed
    private int turns;          //the exact number of turns the user must take to complete a level
    private int winThreshold;   //number of wins needed to increase difficulty
    private int winIndex;       //tracks number of wins needed for increasing difficulty
    private int score;          //the player's score

    public GameStats(){
        this.difficulty = 1;
        this.lives = 3;
        this.turns = this.difficulty;
        this.winThreshold = 3;
        this.winIndex = 0;
        this.score = 0;
    }

    public GameStats(int difficulty, int lives, int turns, int winIndex, int score) {
        this.difficulty = difficulty;
        this.lives = lives;
        this.turns = turns;
        this.winThreshold = 3;
        this.winIndex = winIndex;
        this.score = score;
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

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public void useTurn(){
        this.turns--;
    }

    public int getWinIndex() {
        return winIndex;
    }

    public void setWinIndex(int winIndex) {
        this.winIndex = winIndex;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score){
        this.score += score;
    }
}
