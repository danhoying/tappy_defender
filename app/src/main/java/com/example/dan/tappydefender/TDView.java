package com.example.dan.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    public TDView(Context context) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        player = new PlayerShip(context);
    }

    private void update() {
        player.update();
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            // Lock the area of memory being drawn to
            canvas = ourHolder.lockCanvas();
            // Clear the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            // Draw the player
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
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
