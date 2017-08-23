package com.example.dan.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PlayerShip {

    private final int GRAVITY = -12;
    // Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;
    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    // Stop ship from leaving the screen
    private int maxY;
    private int minY;

    public PlayerShip(Context context) {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;
    }

    public void update() {
        x++;

        if (boosting) {
            speed += 2;
        } else {
            speed -= 5;
        }

        // Constrain top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        // Never stop completely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        // Move the ship up or down
        y -= speed + GRAVITY;

        // But don't let the ship go offscreen
        if (y < minY) {
            y = minY;
        }

        if (y > maxY) {
            y = maxY;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }
}
