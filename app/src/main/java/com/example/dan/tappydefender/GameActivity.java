package com.example.dan.tappydefender;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;

public class GameActivity extends AppCompatActivity {

    // The object which handles the View
    private TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create an instance of the TDView
        // Passes in "this", which is the context of the app
        gameView = new TDView(this, size.x, size.y);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }


    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
