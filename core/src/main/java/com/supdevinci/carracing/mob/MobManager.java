package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.supdevinci.carracing.Player;

import java.util.ArrayList;
import java.util.List;

public class MobManager {
    private static final float MOB_SPEED = 120f;
    private static final float AGRESSIVE_MOB_SPEED = 130f;
    private static final float PASSIVE_MOB_SPEED = 130f;

    private final List<Npc> mobList = new ArrayList<>();
    private final List<AgressiveMob> agressiveMobList = new ArrayList<>();
    private final List<ScaredMob> scaredMobList = new ArrayList<>();

    public void generateMobs(int nbOfMobs, int nbOfPassiveMobs, int nbOfAgressiveMobs, float worldWidth, float worldHeight) {
        mobList.clear();
        scaredMobList.clear();
        agressiveMobList.clear();

        for (int i = 0; i < nbOfMobs; i++) {
            float x = MathUtils.random(worldWidth);
            float y = MathUtils.random(worldHeight);
            mobList.add(new Npc(x, y, MOB_SPEED));
        }

        for (int i = 0; i < nbOfPassiveMobs; i++) {
            float x = MathUtils.random(worldWidth);
            float y = MathUtils.random(worldHeight);
            scaredMobList.add(new ScaredMob(x, y, PASSIVE_MOB_SPEED));
        }

        for (int i = 0; i < nbOfAgressiveMobs; i++) {
            float x = MathUtils.random(worldWidth);
            float y = MathUtils.random(worldHeight);
            agressiveMobList.add(new AgressiveMob(x, y, AGRESSIVE_MOB_SPEED));
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
}
