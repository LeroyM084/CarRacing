package com.supdevinci.carracing;

import com.badlogic.gdx.math.MathUtils;


public class Npc {
    private float x;
    private float y;
    private final float speed;

    private float directionX;
    private float directionY;
    private float changeDirectionTimer;

    public Npc(float x, float y, float speed){
        this.x = x;
        this.y = y;
        this.speed = speed;

        chooseNewDirection();
    }


    public void update(float delta, float worldWidth, float worldHeight) {
        changeDirectionTimer -= delta;

        if(changeDirectionTimer <= 0f) {
            chooseNewDirection();
        }

        x += directionX * speed * delta;
        y += directionY * speed * delta;

        if(x < 0f){
            x = 0f;
            chooseNewDirection();
        }

        if(x>worldWidth){
            x = worldWidth;
            chooseNewDirection();
        }

        if (y < 0f){
            y = 0f;
            chooseNewDirection();
        }

        if(y > worldHeight){
            y = worldHeight;
            chooseNewDirection();
        }
    }

    private void chooseNewDirection() {
        int choice = MathUtils.random(4);

        switch (choice) {
            case 0:
                directionX = 1f;
                directionY = 0f;
                break;
            case 1:
                directionX = -1f;
                directionY = 0f;
                break;
            case 2:
                directionX = 0f;
                directionY = 1f;
                break;
            case 3:
                directionX = 0f;
                directionY = -1f;
                break;
            default:
                directionX = 0f;
                directionY = 0f;
                break;
        }

        changeDirectionTimer = MathUtils.random(0.5f, 2f);
    }

    public float getX(){
        return x;
    }

    public float getY() {
        return y;
    }
}

