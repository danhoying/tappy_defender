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

    private Context context;

    volatile boolean playing;
    private boolean gameEnded;
    Thread gameThread = null;

    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public ArrayList<SpaceDust> dustList = new ArrayList<>();

    private int screenX;
    private int screenY;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;

        screenX = x;
        screenY = y;

        ourHolder = getHolder();
        paint = new Paint();

        startGame();
    }

    private void startGame() {
        // Initialize game objects
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        // Create SpaceDust specs and add to list
        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        // Reset time and distance
        distanceRemaining = 10000; // 10 km
        timeTaken = 0;

        // Get start time
        timeStarted = System.currentTimeMillis();

        gameEnded = false;
    }

    private void update() {
        /* Collision detection on current positions before position is updated and redrawn. Images
        are moved an additional number of pixels left equal to the image horizontal size */
        boolean hitDetected = false;
        int offscreenPosition = enemy1.getBitmap().getWidth() * -1;
        if (Rect.intersects(player.getHitbox(), enemy1.getHitbox())) {
            hitDetected = true;
            enemy1.setX(offscreenPosition);
        }

        if (Rect.intersects(player.getHitbox(), enemy2.getHitbox())) {
            hitDetected = true;
            enemy2.setX(offscreenPosition);
        }

        if (Rect.intersects(player.getHitbox(), enemy3.getHitbox())) {
            hitDetected = true;
            enemy3.setX(offscreenPosition);
        }

        if (hitDetected) {
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                gameEnded = true;
            }
        }

        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }

        if (!gameEnded) {
            // Subtract distance to home based on current speed
            distanceRemaining -= player.getSpeed();

            // Keep track of how long the player has been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        // Completed the game
        if (distanceRemaining < 0) {
            // Check for new fastest time
            if (timeTaken < fastestTime) {
                fastestTime = timeTaken;
            }

            // Avoid negative numbers in HUD
            distanceRemaining = 0;

            // End the game
            gameEnded = true;
        }
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            // Lock the area of memory being drawn to
            canvas = ourHolder.lockCanvas();

            // Clear the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            if (!gameEnded) {
                // Draw the space dust
                paint.setColor(Color.argb(255, 255, 255, 255));
                for (SpaceDust sd : dustList) {
                    canvas.drawPoint(sd.getX(), sd.getY(), paint);
                }

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

                // Draw the player
                canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

                // Draw the enemies
                canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
                canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
                canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);

                // Draw the HUD
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(50);
                canvas.drawText("Fastest: " + fastestTime + "s", 10, 45, paint);
                canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 45, paint);
                canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM",
                        screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield: " + player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS",
                        (screenX / 3) * 2, screenY - 20, paint);
            } else {
               // Show game over screen
                paint.setTextSize(180);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, 100, paint);
                paint.setTextSize(100);
                canvas.drawText("Fastest: " + fastestTime + "s", screenX / 2, 160, paint);

                canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 200, paint);

                canvas.drawText("Distance remaining: " +
                        distanceRemaining / 1000 + " KM",screenX / 2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX / 2, 350, paint);
            }

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
                // If on game over screen, screen touch starts a new game
                if (gameEnded) {
                    startGame();
                }
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
