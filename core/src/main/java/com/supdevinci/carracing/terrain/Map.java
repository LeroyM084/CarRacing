package com.supdevinci.carracing.terrain;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Map {
    public void drawGrass(float world_width, float world_height, float tile_size, ShapeRenderer sr) {
        int columns = MathUtils.ceil(world_width / tile_size);
        int rows = MathUtils.ceil(world_height / tile_size  );

        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                boolean alternate = (x + y) % 2 == 0;
                if (alternate) {
                    sr.setColor(0.29f, 0.58f, 0.24f, 1f);
                } else {
                    sr.setColor(0.26f, 0.53f, 0.22f, 1f);
                }
                sr.rect(x * tile_size, y * tile_size, tile_size, tile_size);
            }
        }
    }
}
