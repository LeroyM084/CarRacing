package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.supdevinci.carracing.Player;


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

    public void draw(ShapeRenderer sr){
        sr.setColor(Color.GOLD);
        sr.circle(this.getX(), this.getY(), 14f);

    }


    public void update(float delta, float worldWidth, float worldHeight, Player player) {
        changeDirectionTimer -= delta;

        if(changeDirectionTimer <= 0f) {
            chooseNewDirection();
        }

        move(directionX, directionY, delta);
        keepInsideWorld(worldWidth, worldHeight);
    }

    protected void move(float moveX, float moveY, float delta) {
        x += moveX * speed * delta;
        y += moveY * speed * delta;
    }

    protected void keepInsideWorld(float worldWidth, float worldHeight) {
        boolean touchedEdge = false;

        if(x < 0f){
            x = 0f;
            touchedEdge = true;
        }

        if(x > worldWidth){
            x = worldWidth;
            touchedEdge = true;
        }

        if (y < 0f){
            y = 0f;
            touchedEdge = true;
        }

        if(y > worldHeight){
            y = worldHeight;
            touchedEdge = true;
        }

        if (touchedEdge) {
            chooseNewDirection();
        }
    }

    protected float getSpeed() {
        return speed;
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
