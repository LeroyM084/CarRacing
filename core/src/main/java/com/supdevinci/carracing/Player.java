package com.supdevinci.carracing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Player {
    private static final float DIAGONAL_COMPONENT = 0.70710677f;
    private static final float ATTACK_DURATION = 0.2f;
    private static final float ATTACK_LENGTH = 32f;
    private static final float ATTACK_WIDTH = 16f;

    private final float size;
    private final float speed;

    private float x;
    private float y;

    private String facingDirection;
    private boolean isMoving;
    private boolean isAttacking;
    private float attackTimer;
    private boolean attackTriggered;

    public Player(float x, float y, float size, float speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.facingDirection = "down";
        this.isMoving = false;
        this.isAttacking = false;
        this.attackTimer = 0f;
        this.attackTriggered = false;
    }

    public void update(float delta, float worldWidth, float worldHeight) {
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.Q)
                || Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX += 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.Z)
                || Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY -= 1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attack();
        }

        attackTimer -= delta;
        if (attackTimer <= 0f) {
            isAttacking = false;
            attackTimer = 0f;
        }

        isMoving = (moveX != 0f || moveY != 0f);

        if (moveX != 0f || moveY != 0f) {
            updateFacingDirection(moveX, moveY);

            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
        }

        x = MathUtils.clamp(x + moveX * speed * delta, size / 2f, worldWidth - size / 2f);
        y = MathUtils.clamp(y + moveY * speed * delta, size / 2f, worldHeight - size / 2f);
    }

    public void attack() {
        if (attackTimer <= 0f) {
            isAttacking = true;
            attackTimer = ATTACK_DURATION;
            attackTriggered = true;
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        float[] attackLine = getAttackLine();
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rectLine(attackLine[0], attackLine[1], attackLine[2], attackLine[3], ATTACK_WIDTH);

        shapeRenderer.setColor(Color.NAVY);
        shapeRenderer.circle(x, y, size / 2f + 8f);

        shapeRenderer.setColor(Color.SCARLET);
        float radius = size / 2f;
        switch (facingDirection) {
            case "up":
                shapeRenderer.triangle(x - radius, y - radius, x + radius, y - radius, x, y + radius);
                break;
            case "up-right":
                shapeRenderer.triangle(x - radius, y - radius * 0.5f, x - radius * 0.5f, y - radius,
                        x + radius, y + radius);
                break;
            case "right":
                shapeRenderer.triangle(x - radius, y - radius, x - radius, y + radius, x + radius, y);
                break;
            case "down-right":
                shapeRenderer.triangle(x - radius, y + radius * 0.5f, x - radius * 0.5f, y + radius,
                        x + radius, y - radius);
                break;
            case "down":
                shapeRenderer.triangle(x - radius, y + radius, x + radius, y + radius, x, y - radius);
                break;
            case "down-left":
                shapeRenderer.triangle(x + radius, y + radius * 0.5f, x + radius * 0.5f, y + radius,
                        x - radius, y - radius);
                break;
            case "left":
                shapeRenderer.triangle(x + radius, y - radius, x + radius, y + radius, x - radius, y);
                break;
            case "up-left":
                shapeRenderer.triangle(x + radius, y - radius * 0.5f, x + radius * 0.5f, y - radius,
                        x - radius, y + radius);
                break;
        }
    }

    public float[] getAttackLine() {
        float radius = size / 2f;
        float[] facingVector = getFacingVector();
        float startX = x + facingVector[0] * radius;
        float startY = y + facingVector[1] * radius;
        float endX = startX + facingVector[0] * ATTACK_LENGTH;
        float endY = startY + facingVector[1] * ATTACK_LENGTH;

        return new float[] { startX, startY, endX, endY };
    }

    private void updateFacingDirection(float moveX, float moveY) {
        if (moveX > 0f && moveY > 0f) {
            facingDirection = "up-right";
        } else if (moveX > 0f && moveY < 0f) {
            facingDirection = "down-right";
        } else if (moveX < 0f && moveY > 0f) {
            facingDirection = "up-left";
        } else if (moveX < 0f && moveY < 0f) {
            facingDirection = "down-left";
        } else if (moveX > 0f) {
            facingDirection = "right";
        } else if (moveX < 0f) {
            facingDirection = "left";
        } else if (moveY > 0f) {
            facingDirection = "up";
        } else if (moveY < 0f) {
            facingDirection = "down";
        }
    }

    private float[] getFacingVector() {
        switch (facingDirection) {
            case "up":
                return new float[] { 0f, 1f };
            case "up-right":
                return new float[] { DIAGONAL_COMPONENT, DIAGONAL_COMPONENT };
            case "right":
                return new float[] { 1f, 0f };
            case "down-right":
                return new float[] { DIAGONAL_COMPONENT, -DIAGONAL_COMPONENT };
            case "down":
                return new float[] { 0f, -1f };
            case "down-left":
                return new float[] { -DIAGONAL_COMPONENT, -DIAGONAL_COMPONENT };
            case "left":
                return new float[] { -1f, 0f };
            case "up-left":
                return new float[] { -DIAGONAL_COMPONENT, DIAGONAL_COMPONENT };
            default:
                return new float[] { 0f, -1f };
        }
    }

    public boolean consumeAttackTriggered() {
        boolean wasTriggered = attackTriggered;
        attackTriggered = false;
        return wasTriggered;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public float getAttackWidth() {
        return ATTACK_WIDTH;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCollisionRadius() {
        return size / 2f;
    }

    public boolean isMoving() {
        return isMoving;
    }
}
