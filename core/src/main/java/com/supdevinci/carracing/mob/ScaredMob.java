package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.supdevinci.carracing.Player;
import com.supdevinci.carracing.physics.PhysicsWorld;

public class ScaredMob extends Npc {
    private static final float FEAR_RADIUS = 180f;
    private static final float SAFE_RADIUS = 260f;
    private static final float PANIC_RADIUS = 36f;

    private boolean fleeing;

    public ScaredMob(float x, float y, float speed, Color color, PhysicsWorld physicsWorld) {
        super(x, y, speed, color, physicsWorld);
    }

    @Override
    public void update(float delta, Player player) {
        if (!isAlive()) {
            return;
        }

        float dx = player.getX() - getX();
        float dy = player.getY() - getY();
        float distanceSquared = dx * dx + dy * dy;
        float fearRadiusSquared = FEAR_RADIUS * FEAR_RADIUS;
        float safeRadiusSquared = SAFE_RADIUS * SAFE_RADIUS;

        if (distanceSquared <= fearRadiusSquared) {
            fleeing = true;
        } else if (distanceSquared >= safeRadiusSquared) {
            fleeing = false;
        }

        if (fleeing) {
            float distance = (float) Math.sqrt(distanceSquared);

            if (distance <= PANIC_RADIUS) {
                moveAwayInRandomDirection(dx, dy);
            } else if (distance > 0f) {
                move(-dx / distance, -dy / distance);
            }
            return;
        }

        super.update(delta, player);
    }

    private void moveAwayInRandomDirection(float dx, float dy) {
        float escapeX = -dx;
        float escapeY = -dy;

        if (escapeX == 0f && escapeY == 0f) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            escapeX = MathUtils.cos(angle);
            escapeY = MathUtils.sin(angle);
        }

        float jitterAngle = MathUtils.random(-0.45f, 0.45f);
        float cos = MathUtils.cos(jitterAngle);
        float sin = MathUtils.sin(jitterAngle);
        float rotatedX = escapeX * cos - escapeY * sin;
        float rotatedY = escapeX * sin + escapeY * cos;
        float length = (float) Math.sqrt(rotatedX * rotatedX + rotatedY * rotatedY);

        if (length == 0f) {
            return;
        }

        move(rotatedX / length, rotatedY / length);
    }
}
