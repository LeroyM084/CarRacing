package com.supdevinci.carracing.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.supdevinci.carracing.Player;
import com.supdevinci.carracing.physics.PhysicsWorld;


public class Npc {
    public static final float COLLISION_RADIUS = 14f;
    private static final float EDGE_EPSILON = 0.5f;

    private float x;
    private float y;
    private final float speed;
    private final Body body;

    private float directionX;
    private float directionY;
    private float changeDirectionTimer;

    private final Color color;
    private int hp;
    public boolean isAlive = true;

    public Npc(float x, float y, float speed, Color color, PhysicsWorld physicsWorld){
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.body = physicsWorld.createDynamicCircle(x, y, COLLISION_RADIUS);
        this.hp = 15;
        this.color = color;

        chooseNewDirection();
    }

    public void draw(ShapeRenderer sr) {
        if (!isAlive) {
            return;
        }

        float barWidth = 28f;
        float barHeight = 4f;
        float barX = getX() - barWidth / 2f;
        float barY = getY() + COLLISION_RADIUS + 6f;

        float hpPercent = (float) hp / 15f;
        hpPercent = MathUtils.clamp(hpPercent, 0f, 1f);

        sr.setColor(Color.BLACK);
        sr.rect(barX, barY, barWidth, barHeight);

        sr.setColor(Color.GREEN);
        sr.rect(barX, barY, barWidth * hpPercent, barHeight);

        sr.setColor(this.color);
        sr.circle(getX(), getY(), COLLISION_RADIUS);
    }


    public void update(float delta, Player player) {
        if (!isAlive) {
            body.setLinearVelocity(0f, 0f);
            return;
        }

        changeDirectionTimer -= delta;

        if(changeDirectionTimer <= 0f) {
            chooseNewDirection();
        }

        move(directionX, directionY);
    }

    protected void move(float moveX, float moveY) {
        body.setLinearVelocity(
                PhysicsWorld.toWorldUnits(moveX * speed),
                PhysicsWorld.toWorldUnits(moveY * speed));
    }

    public void syncFromPhysics(float worldWidth, float worldHeight) {
        x = PhysicsWorld.toPixels(body.getPosition().x);
        y = PhysicsWorld.toPixels(body.getPosition().y);

        if (pushesAgainstWorldEdge(worldWidth, worldHeight)) {
            chooseNewDirection();
        }
    }

    public void takeDamage(int damage) {
        if (!isAlive) {
            return;
        }

        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            isAlive = false;
            body.setLinearVelocity(0f, 0f);
            body.setActive(false);
        }
    }

    protected float getSpeed() {
        return speed;
    }

    private boolean pushesAgainstWorldEdge(float worldWidth, float worldHeight) {
        return (x <= COLLISION_RADIUS + EDGE_EPSILON && directionX < 0f)
                || (x >= worldWidth - COLLISION_RADIUS - EDGE_EPSILON && directionX > 0f)
                || (y <= COLLISION_RADIUS + EDGE_EPSILON && directionY < 0f)
                || (y >= worldHeight - COLLISION_RADIUS - EDGE_EPSILON && directionY > 0f);
    }

    private void chooseNewDirection() {
        int choice = MathUtils.random(4);

        switch (choice) {
            case 0:
                directionX = 1f;
                directionY = 0f;
                break;
            case 1:
                directionX = -1f;
                directionY = 0f;
                break;
            case 2:
                directionX = 0f;
                directionY = 1f;
                break;
            case 3:
                directionX = 0f;
                directionY = -1f;
                break;
            default:
                directionX = 0f;
                directionY = 0f;
                break;
        }

        changeDirectionTimer = MathUtils.random(0.5f, 2f);
    }

    public float getX(){
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCollisionRadius() {
        return COLLISION_RADIUS;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int hp() {
        return hp;
    }
}
