package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.supdevinci.carracing.Player;

public class AgressiveMob extends Npc{
    private static final float AGGRO_RADIUS = 250f;


    public AgressiveMob(float x, float y, float speed, Color color) {
        super(x, y, speed, color);
    }

    @Override
    public void update(float delta, float worldWidth, float worldHeight, Player player) {
        if (!isAlive()) {
            return;
        }

        float dx = player.getX() - getX();
        float dy = player.getY() - getY();
        float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared <= AGGRO_RADIUS * AGGRO_RADIUS) {
            float distance = (float) Math.sqrt(distanceSquared);
            if (distance > 0f) {
                move(dx / distance, dy / distance, delta);
                keepInsideWorld(worldWidth, worldHeight);
                return;
            }
        }

        super.update(delta, worldWidth, worldHeight, player);
    }
}
