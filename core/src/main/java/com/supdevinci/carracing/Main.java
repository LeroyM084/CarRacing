package com.supdevinci.carracing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.supdevinci.carracing.mob.MobManager;
import com.supdevinci.carracing.terrain.Map;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private static final float VIEWPORT_WIDTH = 640f;
    private static final float VIEWPORT_HEIGHT = 480f;
    private static final float WORLD_WIDTH = 2200f;
    private static final float WORLD_HEIGHT = 2200f;
    private static final float TILE_SIZE = 32f;
    private static final float PLAYER_SIZE = 28f;
    private static final float PLAYER_SPEED = 800f;
    private final Map map = new Map();
    private final MobManager mobManager = new MobManager();

    private enum GameState {
        MENU,
        PLAYING
    }

    private Menu menu;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport gameViewport;
    private Player player;

    private GameState gameState;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        menu = new Menu(skin, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, this::startGame);
        shapeRenderer = new ShapeRenderer();
        // spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, PLAYER_SIZE, PLAYER_SPEED);

        mobManager.generateMobs(2, 5, 5, WORLD_WIDTH, WORLD_HEIGHT);
        showMenu();
    }

    private void showMenu() {
        gameState = GameState.MENU;
        menu.show();
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
        menu.render(delta);
    }

    private void updateGame(float delta) {
        player.update(delta, WORLD_WIDTH, WORLD_HEIGHT);
        updateCameraPosition();
        mobManager.update(delta, WORLD_WIDTH, WORLD_HEIGHT, player);
    }

    private void updateCameraPosition() {
        camera.position.set(
                MathUtils.clamp(player.getX(), VIEWPORT_WIDTH / 2f, WORLD_WIDTH - VIEWPORT_WIDTH / 2f),
                MathUtils.clamp(player.getY(), VIEWPORT_HEIGHT / 2f, WORLD_HEIGHT - VIEWPORT_HEIGHT / 2f),
                0f);
        camera.update();
    }

    private void renderGame() {
        ScreenUtils.clear(0.35f, 0.6f, 0.3f, 1f);

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        map.drawGrass(WORLD_WIDTH, WORLD_HEIGHT, TILE_SIZE, shapeRenderer);
        player.render(shapeRenderer);
        mobManager.draw(shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        menu.resize(width, height);
        gameViewport.update(width, height);
        updateCameraPosition();
    }

    @Override
    public void dispose() {
        menu.dispose();
        skin.dispose();
        shapeRenderer.dispose();
    }
}
