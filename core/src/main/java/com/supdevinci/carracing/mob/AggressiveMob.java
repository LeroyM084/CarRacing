package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.supdevinci.carracing.Player;
import com.supdevinci.carracing.physics.PhysicsWorld;

public class AggressiveMob extends Npc {
    private static final float AGGRO_RADIUS = 250f;

    public AggressiveMob(float x, float y, float speed, Color color, PhysicsWorld physicsWorld) {
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

        if (distanceSquared <= AGGRO_RADIUS * AGGRO_RADIUS) {
            float distance = (float) Math.sqrt(distanceSquared);
            if (distance > 0f) {
                move(dx / distance, dy / distance);
                return;
            }
        }

        super.update(delta, player);
    }
}
