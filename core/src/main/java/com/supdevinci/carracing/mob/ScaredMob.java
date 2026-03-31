package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.supdevinci.carracing.Player;

public class ScaredMob extends Npc{
    private static final float FEAR_RADIUS = 10f;

    public ScaredMob(float x, float y, float speed, Color color ) {
        super(x, y, speed,color);
    }

    @Override
    public void update(float delta, float worldWidth, float worldHeight, Player player) {
        if (!isAlive()) {
            return;
        }

        float dx = player.getX() - getX();
        float dy = player.getY() - getY();
        float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared <= FEAR_RADIUS * FEAR_RADIUS) {
            float distance = (float) Math.sqrt(distanceSquared);
            if (distance > 0f) {
                move(-dx / distance, -dy / distance, delta);
                keepInsideWorld(worldWidth, worldHeight);
                return;
            }
        }

        super.update(delta, worldWidth, worldHeight, player);
    }
}
