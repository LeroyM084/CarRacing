package com.supdevinci.carracing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static final float VIEWPORT_WIDTH = 640f;
    private static final float VIEWPORT_HEIGHT = 480f;
    private static final float WORLD_WIDTH = 2200f;
    private static final float WORLD_HEIGHT = 2200f;
    private static final float TILE_SIZE = 32f;
    private static final float PLAYER_SIZE = 28f;
    private static final float PLAYER_SPEED = 800f;
    private final List<Npc> npcList = new ArrayList<>();


    private enum GameState {
        MENU,
        PLAYING
    }

    private Stage menuStage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport gameViewport;

    private GameState gameState;
    private float playerX;
    private float playerY;


    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        menuStage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        playerX = WORLD_WIDTH / 2f;
        playerY = WORLD_HEIGHT / 2f;

        createMenu();
        createNPC();
        showMenu();
    }

    private void createNPC() {
        int nbNPCs = MathUtils.random(10);

        npcList.clear();

        for (int i = 0; i < nbNPCs; i++) {
            float spawnX = MathUtils.random(0, WORLD_WIDTH);
            float spawnY = MathUtils.random(0, WORLD_HEIGHT);

            Npc npc = new Npc(spawnX, spawnY, 150f);
            npcList.add(npc);
        }

    }

    private void updateNPC(float delta) {
        for (Npc npc : npcList) {
            npc.update(delta, WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    private void createMenu() {
        Table root = new Table();
        root.setFillParent(true);

        TextButton startButton = new TextButton("Commencer", skin);
        startButton.pad(12f, 24f, 12f, 24f);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                startGame();
            }
        });

        root.add(startButton).minWidth(220f);
        menuStage.addActor(root);
    }

    private void showMenu() {
        gameState = GameState.MENU;
        Gdx.input.setInputProcessor(menuStage);
    }

    private void startGame() {
        gameState = GameState.PLAYING;
        Gdx.input.setInputProcessor(null);
        updateCameraPosition();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        if (gameState == GameState.MENU) {
            renderMenu(delta);
            return;
        }

        updateGame(delta);
        renderGame();
    }

    private void renderMenu(float delta) {
        ScreenUtils.clear(0.08f, 0.11f, 0.08f, 1f);
        menuStage.act(delta);
        menuStage.draw();
    }

    private void updateGame(float delta) {
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY -= 1f;
        }

        if (moveX != 0f || moveY != 0f) {
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
        }

        playerX = MathUtils.clamp(playerX + moveX * PLAYER_SPEED * delta, PLAYER_SIZE / 2f, WORLD_WIDTH - PLAYER_SIZE / 2f);
        playerY = MathUtils.clamp(playerY + moveY * PLAYER_SPEED * delta, PLAYER_SIZE / 2f, WORLD_HEIGHT - PLAYER_SIZE / 2f);
        updateCameraPosition();
        updateNPC(delta);

    }

    private void updateCameraPosition() {
        camera.position.set(
            MathUtils.clamp(playerX, VIEWPORT_WIDTH / 2f, WORLD_WIDTH - VIEWPORT_WIDTH / 2f),
            MathUtils.clamp(playerY, VIEWPORT_HEIGHT / 2f, WORLD_HEIGHT - VIEWPORT_HEIGHT / 2f),
            0f
        );
        camera.update();
    }

    private void renderGame() {
        ScreenUtils.clear(0.35f, 0.6f, 0.3f, 1f);

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawGrass();
        drawPlayer();
        for (Npc npc : npcList) {
            drawNPC(npc);
        }
        shapeRenderer.end();
    }

    private void drawGrass() {
        int columns = MathUtils.ceil(WORLD_WIDTH / TILE_SIZE);
        int rows = MathUtils.ceil(WORLD_HEIGHT / TILE_SIZE);

        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                boolean alternate = (x + y) % 2 == 0;
                if (alternate) {
                    shapeRenderer.setColor(0.29f, 0.58f, 0.24f, 1f);
                } else {
                    shapeRenderer.setColor(0.26f, 0.53f, 0.22f, 1f);
                }
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawPlayer() {
        shapeRenderer.setColor(Color.NAVY);
        shapeRenderer.circle(playerX, playerY, PLAYER_SIZE / 2f + 8f);

        shapeRenderer.setColor(Color.SCARLET);
        shapeRenderer.circle(playerX, playerY, PLAYER_SIZE / 2f);
    }

    private void drawNPC(Npc npc){
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.circle(npc.getX(), npc.getY(), 14f);

    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        menuStage.getViewport().update(width, height, true);
        gameViewport.update(width, height);
        updateCameraPosition();
    }

    @Override
    public void dispose() {
        menuStage.dispose();
        skin.dispose();
        shapeRenderer.dispose();
    }
}

