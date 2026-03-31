package com.supdevinci.carracing.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class PhysicsWorld implements Disposable {
    private static final float PIXELS_PER_METER = 32f;
    private static final float WALL_THICKNESS = 32f;
    private static final float MAX_FRAME_TIME = 0.25f;
    private static final float FIXED_TIME_STEP = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private final World world;
    private float accumulator;

    public PhysicsWorld(float worldWidth, float worldHeight) {
        world = new World(Vector2.Zero, true);
        createWorldBounds(worldWidth, worldHeight);
    }

    public Body createDynamicCircle(float x, float y, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(toWorldUnits(x), toWorldUnits(y));
        bodyDef.fixedRotation = true;
        bodyDef.bullet = true;

        Body body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(toWorldUnits(radius));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        body.setSleepingAllowed(false);
        shape.dispose();
        return body;
    }

    public void step(float delta) {
        accumulator += Math.min(delta, MAX_FRAME_TIME);
        while (accumulator >= FIXED_TIME_STEP) {
            world.step(FIXED_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= FIXED_TIME_STEP;
        }
    }

    public World getWorld() {
        return world;
    }

    public static float toWorldUnits(float pixels) {
        return pixels / PIXELS_PER_METER;
    }

    public static float toPixels(float worldUnits) {
        return worldUnits * PIXELS_PER_METER;
    }

    private void createWorldBounds(float worldWidth, float worldHeight) {
        createWall(-WALL_THICKNESS / 2f, worldHeight / 2f, WALL_THICKNESS / 2f, worldHeight / 2f);
        createWall(worldWidth + WALL_THICKNESS / 2f, worldHeight / 2f, WALL_THICKNESS / 2f, worldHeight / 2f);
        createWall(worldWidth / 2f, -WALL_THICKNESS / 2f, worldWidth / 2f, WALL_THICKNESS / 2f);
        createWall(worldWidth / 2f, worldHeight + WALL_THICKNESS / 2f, worldWidth / 2f, WALL_THICKNESS / 2f);
    }

    private void createWall(float centerX, float centerY, float halfWidth, float halfHeight) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(toWorldUnits(centerX), toWorldUnits(centerY));

        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(toWorldUnits(halfWidth), toWorldUnits(halfHeight));
        body.createFixture(shape, 0f);
        shape.dispose();
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}
