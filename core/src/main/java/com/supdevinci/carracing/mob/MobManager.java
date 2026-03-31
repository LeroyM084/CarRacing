package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.supdevinci.carracing.Player;
import com.supdevinci.carracing.physics.PhysicsWorld;

import java.util.ArrayList;
import java.util.List;

public class MobManager {
    private static final int ATTACK_DAMAGE = 10;
    private static final float MOB_SPEED = 120f;
    private static final float AGGRESSIVE_MOB_SPEED = 130f;
    private static final float SCARED_MOB_SPEED = 130f;

    private final List<Npc> mobList = new ArrayList<>();
    private final List<AggressiveMob> aggressiveMobList = new ArrayList<>();
    private final List<ScaredMob> scaredMobList = new ArrayList<>();

    private record SpawnCircle(float x, float y, float radius) {
    }

    public void generateMobs(int nbOfMobs, int nbOfScaredMobs, int nbOfAggressiveMobs, float worldWidth,
            float worldHeight, Player player, PhysicsWorld physicsWorld) {
        mobList.clear();
        scaredMobList.clear();
        aggressiveMobList.clear();
        List<SpawnCircle> occupiedAreas = new ArrayList<>();
        occupiedAreas.add(new SpawnCircle(player.getX(), player.getY(), player.getCollisionRadius()));

        spawnNeutralMobs(nbOfMobs, worldWidth, worldHeight, physicsWorld, occupiedAreas);
        spawnScaredMobs(nbOfScaredMobs, worldWidth, worldHeight, physicsWorld, occupiedAreas);
        spawnAggressiveMobs(nbOfAggressiveMobs, worldWidth, worldHeight, physicsWorld, occupiedAreas);
    }

    public void update(float delta, Player player) {
        updateMobs(mobList, delta, player);
        updateMobs(scaredMobList, delta, player);
        updateMobs(aggressiveMobList, delta, player);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        drawMobs(mobList, shapeRenderer);
        drawMobs(scaredMobList, shapeRenderer);
        drawMobs(aggressiveMobList, shapeRenderer);
    }

    public void syncFromPhysics(float worldWidth, float worldHeight) {
        syncMobs(mobList, worldWidth, worldHeight);
        syncMobs(scaredMobList, worldWidth, worldHeight);
        syncMobs(aggressiveMobList, worldWidth, worldHeight);
    }

    public void handlePlayerAttack(Player player) {
        if (!player.consumeAttackTriggered()) {
            return;
        }

        checkAttackCollisions(player, mobList);
        checkAttackCollisions(player, scaredMobList);
        checkAttackCollisions(player, aggressiveMobList);
    }

    private void spawnNeutralMobs(int count, float worldWidth, float worldHeight, PhysicsWorld physicsWorld,
            List<SpawnCircle> occupiedAreas) {
        for (int i = 0; i < count; i++) {
            SpawnCircle spawn = findSpawnPosition(worldWidth, worldHeight, occupiedAreas);
            mobList.add(new Npc(spawn.x(), spawn.y(), MOB_SPEED, Color.GREEN, physicsWorld));
            occupiedAreas.add(spawn);
        }
    }

    private void spawnScaredMobs(int count, float worldWidth, float worldHeight, PhysicsWorld physicsWorld,
            List<SpawnCircle> occupiedAreas) {
        for (int i = 0; i < count; i++) {
            SpawnCircle spawn = findSpawnPosition(worldWidth, worldHeight, occupiedAreas);
            scaredMobList.add(new ScaredMob(spawn.x(), spawn.y(), SCARED_MOB_SPEED, Color.CYAN, physicsWorld));
            occupiedAreas.add(spawn);
        }
    }

    private void spawnAggressiveMobs(int count, float worldWidth, float worldHeight, PhysicsWorld physicsWorld,
            List<SpawnCircle> occupiedAreas) {
        for (int i = 0; i < count; i++) {
            SpawnCircle spawn = findSpawnPosition(worldWidth, worldHeight, occupiedAreas);
            aggressiveMobList
                    .add(new AggressiveMob(spawn.x(), spawn.y(), AGGRESSIVE_MOB_SPEED, Color.RED, physicsWorld));
            occupiedAreas.add(spawn);
        }
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

    private void updateMobs(List<? extends Npc> mobs, float delta, Player player) {
        for (Npc mob : mobs) {
            mob.update(delta, player);
        }
    }

    private void drawMobs(List<? extends Npc> mobs, ShapeRenderer shapeRenderer) {
        for (Npc mob : mobs) {
            mob.draw(shapeRenderer);
        }
    }

    private void syncMobs(List<? extends Npc> mobs, float worldWidth, float worldHeight) {
        for (Npc mob : mobs) {
            mob.syncFromPhysics(worldWidth, worldHeight);
        }
    }

    private SpawnCircle findSpawnPosition(float worldWidth, float worldHeight, List<SpawnCircle> occupiedAreas) {
        SpawnCircle fallback = null;

        for (int attempt = 0; attempt < 100; attempt++) {
            float x = MathUtils.random(Npc.COLLISION_RADIUS, worldWidth - Npc.COLLISION_RADIUS);
            float y = MathUtils.random(Npc.COLLISION_RADIUS, worldHeight - Npc.COLLISION_RADIUS);
            SpawnCircle candidate = new SpawnCircle(x, y, Npc.COLLISION_RADIUS);

            if (fallback == null) {
                fallback = candidate;
            }

            if (!overlapsExisting(candidate, occupiedAreas)) {
                return candidate;
            }
        }

        if (fallback == null) {
            return new SpawnCircle(Npc.COLLISION_RADIUS, Npc.COLLISION_RADIUS, Npc.COLLISION_RADIUS);
        }

        return fallback;
    }

    private boolean overlapsExisting(SpawnCircle candidate, List<SpawnCircle> occupiedAreas) {
        for (SpawnCircle occupied : occupiedAreas) {
            float dx = occupied.x() - candidate.x();
            float dy = occupied.y() - candidate.y();
            float minDistance = occupied.radius() + candidate.radius() + 4f;

            if (dx * dx + dy * dy < minDistance * minDistance) {
                return true;
            }
        }

        return false;
    }
}
