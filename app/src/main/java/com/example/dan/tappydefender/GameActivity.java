package com.example.dan.tappydefender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    // The object which handles the View
    private TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of the TDView
        // Passes in "this", which is the context of the app
        gameView = new TDView(this);
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
