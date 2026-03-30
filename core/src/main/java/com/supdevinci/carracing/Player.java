package com.supdevinci.carracing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Player {
    private final float size;
    private final float speed;

    private float x;
    private float y;

    public Player(float x, float y, float size, float speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
    }



    public void update(float delta, float worldWidth, float worldHeight) {
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY -= 1f;
        }

        if (moveX != 0f || moveY != 0f) {
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
        }

        x = MathUtils.clamp(x + moveX * speed * delta, size / 2f, worldWidth - size / 2f);
        y = MathUtils.clamp(y + moveY * speed * delta, size / 2f, worldHeight - size / 2f);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.NAVY);
        shapeRenderer.circle(x, y, size / 2f + 8f);

        shapeRenderer.setColor(Color.SCARLET);
        shapeRenderer.circle(x, y, size / 2f);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
