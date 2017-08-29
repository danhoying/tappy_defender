package com.example.dan.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

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
        /* Collision detection on current positions before position is updated and redrawn. Images
        are moved an additional number of pixels left equal to the image horizontal size */
        int offscreenPosition = enemy1.getBitmap().getWidth() * -1;
        if (Rect.intersects(player.getHitbox(), enemy1.getHitbox())) {
            enemy1.setX(offscreenPosition);
        }

        if (Rect.intersects(player.getHitbox(), enemy2.getHitbox())) {
            enemy2.setX(offscreenPosition);
        }

        if (Rect.intersects(player.getHitbox(), enemy3.getHitbox())) {
            enemy3.setX(offscreenPosition);
        }

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

            // For debugging. Switch to white pixels and draw hit boxes
//            paint.setColor(Color.argb(255, 255, 255, 255));
//            canvas.drawRect(player.getHitbox().left,
//                    player.getHitbox().top,
//                    player.getHitbox().right,
//                    player.getHitbox().bottom, paint);
//            canvas.drawRect(enemy1.getHitbox().left,
//                    enemy1.getHitbox().top,
//                    enemy1.getHitbox().right,
//                    enemy1.getHitbox().bottom, paint);
//            canvas.drawRect(enemy2.getHitbox().left,
//                    enemy2.getHitbox().top,
//                    enemy2.getHitbox().right,
//                    enemy2.getHitbox().bottom, paint);
//            canvas.drawRect(enemy3.getHitbox().left,
//                    enemy3.getHitbox().top,
//                    enemy3.getHitbox().right,
//                    enemy3.getHitbox().bottom, paint);

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

            // Draw the HUD
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(25);
            canvas.drawText("Fastest: " + fastestTime + "s", 10, 20, paint);
            canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 20, paint);
            canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM",
                    screenX / 3, screenY - 20, paint);
            canvas.drawText("Shield: " + player.getShieldStrength(), 10, screenY - 20, paint);
            canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS",
                    (screenX / 3) * 2, screenY - 20, paint);

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
