package com.supdevinci.carracing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Menu {
    private final Stage stage;

    public Menu(Skin skin, float viewportWidth, float viewportHeight, Runnable onStart) {
        stage = new Stage(new FitViewport(viewportWidth, viewportHeight));

        Table root = new Table();
        root.setFillParent(true);

        TextButton startButton = new TextButton("Commencer", skin);
        startButton.pad(12f, 24f, 12f, 24f);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                onStart.run();
            }
        });

        root.add(startButton).minWidth(220f);
        stage.addActor(root);
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.11f, 0.08f, 1f);
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}
