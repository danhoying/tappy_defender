package com.example.dan.tappydefender;

import android.content.Context;
import android.view.SurfaceView;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;

    public TDView(Context context) {
        super(context);
    }

    private void update() {

    }

    private void draw() {

    }

    private void control() {

    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    // Clean up thread if game is interrupted
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    // Make a new thread and start it when game is resumed
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
