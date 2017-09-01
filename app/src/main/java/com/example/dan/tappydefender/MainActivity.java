package com.example.dan.tappydefender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs;
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);

        buttonPlay.setOnClickListener(this);
        long fastestTime = prefs.getLong("fastestTime", 1000000);
        textFastestTime.setText("Fastest Time: " + fastestTime);
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
        finish();
    }
}
