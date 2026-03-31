package com.supdevinci.carracing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.supdevinci.carracing.physics.PhysicsWorld;

public class Player {
    private static final float ATTACK_DURATION = 0.2f;
    private static final float ATTACK_LENGTH = 32f;
    private static final float ATTACK_WIDTH = 16f;

    private final float size;
    private final float speed;
    private final Body body;

    private float x;
    private float y;

    private float facingX;
    private float facingY;
    private boolean isMoving;
    private boolean isAttacking;
    private float attackTimer;
    private boolean attackTriggered;

    public Player(float x, float y, float size, float speed, PhysicsWorld physicsWorld) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.body = physicsWorld.createDynamicCircle(x, y, size / 2f);
        this.facingX = 0f;
        this.facingY = -1f;
        this.isMoving = false;
        this.isAttacking = false;
        this.attackTimer = 0f;
        this.attackTriggered = false;
    }

    public void update(float delta, float mouseWorldX, float mouseWorldY) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attack();
        }

        attackTimer -= delta;
        if (attackTimer <= 0f) {
            isAttacking = false;
            attackTimer = 0f;
        }

        updateFacingDirection(mouseWorldX, mouseWorldY);

        isMoving = true;

        body.setLinearVelocity(
                PhysicsWorld.toWorldUnits(facingX * speed),
                PhysicsWorld.toWorldUnits(facingY * speed));
    }

    public void syncFromPhysics() {
        x = PhysicsWorld.toPixels(body.getPosition().x);
        y = PhysicsWorld.toPixels(body.getPosition().y);
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
        float perpendicularX = -facingY;
        float perpendicularY = facingX;
        float tipX = x + facingX * radius * 1.35f;
        float tipY = y + facingY * radius * 1.35f;
        float backCenterX = x - facingX * radius * 0.75f;
        float backCenterY = y - facingY * radius * 0.75f;
        float leftX = backCenterX + perpendicularX * radius * 0.95f;
        float leftY = backCenterY + perpendicularY * radius * 0.95f;
        float rightX = backCenterX - perpendicularX * radius * 0.95f;
        float rightY = backCenterY - perpendicularY * radius * 0.95f;
        shapeRenderer.triangle(leftX, leftY, rightX, rightY, tipX, tipY);
    }

    public float[] getAttackLine() {
        float radius = size / 2f;
        float startX = x + facingX * radius;
        float startY = y + facingY * radius;
        float endX = startX + facingX * ATTACK_LENGTH;
        float endY = startY + facingY * ATTACK_LENGTH;

        return new float[] { startX, startY, endX, endY };
    }

    private void updateFacingDirection(float targetX, float targetY) {
        float directionX = targetX - x;
        float directionY = targetY - y;
        float lengthSquared = directionX * directionX + directionY * directionY;

        if (lengthSquared > 0.0001f) {
            float length = (float) Math.sqrt(lengthSquared);
            facingX = directionX / length;
            facingY = directionY / length;
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
