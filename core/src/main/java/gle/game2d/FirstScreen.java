package gle.game2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class FirstScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private SpriteBatch batch;
    private Player player;

    private static final int WORLD_W = 480;
    private static final int WORLD_H = 320;

    public FirstScreen(Main game) { this.game = game; }

    @Override
    public void show() {
        map = new TmxMapLoader().load("maps/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        CollisionMap collisions = new CollisionMap(map, "Collision");

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_W, WORLD_H, camera);
        camera.position.set(WORLD_W / 2, WORLD_H / 2, 0);
        camera.update();

        batch = new SpriteBatch();
        player = new Player(collisions);

        try {
            MapObject obj = map.getLayers().get("Objects").getObjects().get("PlayerSpawn");
            RectangleMapObject r = (RectangleMapObject) obj;
            float sx = r.getRectangle().x;
            float sy = r.getRectangle().y;
            player.setCenter(sx, sy);
        } catch (Exception ignored) {}
    }

    @Override
    public void render(float delta) {
        player.update(delta);

        float camX = player.getCenterX();
        float camY = player.getCenterY();
        float halfW = (float) (viewport.getWorldWidth() / 2.0);
        float halfH = (float) (viewport.getWorldHeight() / 2.0);
        if (camX < halfW) camX = halfW;
        if (camX > WORLD_W - halfW) camX = WORLD_W - halfW;
        if (camY < halfH) camY = halfH;
        if (camY > WORLD_H - halfH) camY = WORLD_H - halfH;
        camera.position.set(camX, camY, 0);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();
    }

    @Override public void resize(int w, int h) {
        if (w <= 0 || h <= 0) return;
        viewport.update(w, h, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        renderer.dispose();
        map.dispose();
        batch.dispose();
        player.dispose();
    }
}
