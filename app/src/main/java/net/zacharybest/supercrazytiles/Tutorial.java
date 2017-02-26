package net.zacharybest.supercrazytiles;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * The activity for the tutorial.
 * Created by Zac on 2/12/2017.
 */

public class Tutorial extends Activity{

    private ArrayList<Integer> pages;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pages = new ArrayList<>();
        pages.add(R.layout.tutorial_page_welcome);
        pages.add(R.layout.tutorial_page_one);
        pages.add(R.layout.tutorial_page_two);
        pages.add(R.layout.tutorial_page_three);
        pages.add(R.layout.tutorial_page_four);
        setContentView(pages.get(currentIndex));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == event.ACTION_UP) {
            currentIndex++;
            if (currentIndex < pages.size()) {
                setContentView(pages.get(currentIndex));
            } else {
                this.finish();
            }
        }
        return true;
    }

}
