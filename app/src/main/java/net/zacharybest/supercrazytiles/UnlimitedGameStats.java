package net.zacharybest.supercrazytiles;

import java.text.DecimalFormatSymbols;

/**
 * This mode is used for Unlimited Lives
 * Created by Zac on 1/27/2017.
 */

public class UnlimitedGameStats extends GameStats {

    @Override
    public void handleLoss() {
        turns = difficulty;
    }

    @Override
    public String getLivesString(){
        return DecimalFormatSymbols.getInstance().getInfinity();
    }
}
