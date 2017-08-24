package com.example.dan.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;

    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public ArrayList<SpaceDust> dustList = new ArrayList<>();

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    public TDView(Context context, int x, int y) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();

        player = new PlayerShip(context, x, y);
        enemy1 = new EnemyShip(context, x, y);
        enemy2 = new EnemyShip(context, x, y);
        enemy3 = new EnemyShip(context, x, y);

        // Create SpaceDust specs and add to list
        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(x, y);
            dustList.add(spec);
        }
    }

    private void update() {
        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            // Lock the area of memory being drawn to
            canvas = ourHolder.lockCanvas();

            // Clear the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Draw the space dust
            paint.setColor(Color.argb(255, 255, 255, 255));
            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // Draw the player
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            // Draw the enemies
            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;
        }
        return true;
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
