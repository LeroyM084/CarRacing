package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.supdevinci.carracing.Player;


public class Npc {
    private static final float COLLISION_RADIUS = 14f;

    private float x;
    private float y;
    private final float speed;

    private float directionX;
    private float directionY;
    private float changeDirectionTimer;

    private final Color color;
    private int hp;
    public boolean isAlive = true;

    public Npc(float x, float y, float speed, Color color){
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.hp = 15;
        this.color = color;

        chooseNewDirection();
    }

    public void draw(ShapeRenderer sr) {
        if (!isAlive) {
            return;
        }

        float barWidth = 28f;
        float barHeight = 4f;
        float barX = getX() - barWidth / 2f;
        float barY = getY() + COLLISION_RADIUS + 6f;

        float hpPercent = (float) hp / 15f;
        hpPercent = MathUtils.clamp(hpPercent, 0f, 1f);

        sr.setColor(Color.BLACK);
        sr.rect(barX, barY, barWidth, barHeight);

        sr.setColor(Color.GREEN);
        sr.rect(barX, barY, barWidth * hpPercent, barHeight);

        sr.setColor(this.color);
        sr.circle(getX(), getY(), COLLISION_RADIUS);
    }


    public void update(float delta, float worldWidth, float worldHeight, Player player) {
        if (!isAlive) {
            return;
        }

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

    public void takeDamage(int damage) {
        if (!isAlive) {
            return;
        }

        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            isAlive = false;
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

    public float getCollisionRadius() {
        return COLLISION_RADIUS;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int hp() {
        return hp;
    }
}
