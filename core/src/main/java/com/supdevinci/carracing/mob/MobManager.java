package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.supdevinci.carracing.Player;

import java.util.ArrayList;
import java.util.List;

public class MobManager {
    private static final int ATTACK_DAMAGE = 10;
    private static final float MOB_SPEED = 120f;
    private static final float AGRESSIVE_MOB_SPEED = 130f;
    private static final float PASSIVE_MOB_SPEED = 130f;

    private final List<Npc> mobList = new ArrayList<>();
    private final List<AgressiveMob> agressiveMobList = new ArrayList<>();
    private final List<ScaredMob> scaredMobList = new ArrayList<>();

    public void generateMobs(int nbOfMobs, int nbOfPassiveMobs, int nbOfAgressiveMobs, float worldWidth,
            float worldHeight) {
        mobList.clear();
        scaredMobList.clear();
        agressiveMobList.clear();

        for (int i = 0; i < nbOfMobs; i++) {
            float x = MathUtils.random(worldWidth);
            float y = MathUtils.random(worldHeight);
            mobList.add(new Npc(x, y, MOB_SPEED, Color.GREEN));
        }

        for (int i = 0; i < nbOfPassiveMobs; i++) {
            float x = MathUtils.random(worldWidth);
            float y = MathUtils.random(worldHeight);
            scaredMobList.add(new ScaredMob(x, y, PASSIVE_MOB_SPEED, Color.CYAN));
        }

        for (int i = 0; i < nbOfAgressiveMobs; i++) {
            float x = MathUtils.random(worldWidth);
            float y = MathUtils.random(worldHeight);
            agressiveMobList.add(new AgressiveMob(x, y, AGRESSIVE_MOB_SPEED, Color.RED));
        }
    }

    public void update(float delta, float worldWidth, float worldHeight, Player player) {
        for (Npc mob : mobList) {
            mob.update(delta, worldWidth, worldHeight, player);
        }

        for (ScaredMob mob : scaredMobList) {
            mob.update(delta, worldWidth, worldHeight, player);
        }

        for (AgressiveMob mob : agressiveMobList) {
            mob.update(delta, worldWidth, worldHeight, player);
        }

        if (player.consumeAttackTriggered()) {
            handleAttackCollisions(player);
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        for (Npc mob : mobList) {
            mob.draw(shapeRenderer);
        }

        for (ScaredMob mob : scaredMobList) {
            mob.draw(shapeRenderer);
        }

        for (AgressiveMob mob : agressiveMobList) {
            mob.draw(shapeRenderer);
        }
    }

    private void handleAttackCollisions(Player player) {
        checkAttackCollisions(player, mobList);
        checkAttackCollisions(player, scaredMobList);
        checkAttackCollisions(player, agressiveMobList);
    }

    private void checkAttackCollisions(Player player, List<? extends Npc> mobs) {
        float[] attackLine = player.getAttackLine();
        float x1 = attackLine[0];
        float y1 = attackLine[1];
        float x2 = attackLine[2];
        float y2 = attackLine[3];
        float attackRadius = player.getAttackWidth() / 2f;

        for (Npc mob : mobs) {
            if (!mob.isAlive()) {
                continue;
            }

            if (isHitByAttackLine(x1, y1, x2, y2, attackRadius, mob)) {
                mob.takeDamage(ATTACK_DAMAGE);
                System.out.println("Mob hit by attack! HP: " + mob.hp());
                if (mob.hp() < 1) {
                    System.out.println("Mob died from attack!");
                }
            }
        }
    }

    private boolean isHitByAttackLine(float x1, float y1, float x2, float y2, float attackRadius, Npc mob) {
        float mobX = mob.getX();
        float mobY = mob.getY();
        float hitRadius = mob.getCollisionRadius() + attackRadius;

        float dx = x2 - x1;
        float dy = y2 - y1;
        float lengthSq = dx * dx + dy * dy;

        if (lengthSq == 0) {
            float dist = (float) Math.sqrt((mobX - x1) * (mobX - x1) + (mobY - y1) * (mobY - y1));
            return dist <= hitRadius;
        }

        float t = ((mobX - x1) * dx + (mobY - y1) * dy) / lengthSq;
        t = Math.max(0, Math.min(1, t));

        float closestX = x1 + t * dx;
        float closestY = y1 + t * dy;

        float distX = mobX - closestX;
        float distY = mobY - closestY;
        float distSq = distX * distX + distY * distY;

        return distSq <= hitRadius * hitRadius;
    }
}
