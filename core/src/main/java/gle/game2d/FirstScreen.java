package gle.game2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
    private EnemyManager enemyManager;
    private HealthBar healthBar;
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    private float worldW;
    private float worldH;

    public FirstScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load("maps/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        // Récupérer les dimensions de la map
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        int mapWidthInTiles = layer.getWidth();
        int mapHeightInTiles = layer.getHeight();
        int tileSize = (int) layer.getTileWidth();

        worldW = mapWidthInTiles * tileSize;
        worldH = mapHeightInTiles * tileSize;

        CollisionMap collisions = new CollisionMap(map, "Collision");

        camera = new OrthographicCamera();
        viewport = new FitViewport(worldW, worldH, camera);
        camera.position.set(worldW / 2, worldH / 2, 0);
        camera.update();

        batch = new SpriteBatch();
        player = new Player(collisions);

        // Charger la position du joueur depuis Tiled
        try {
            MapObject obj = map.getLayers().get("Object").getObjects().get("Player");
            float sx = obj.getProperties().get("x", Float.class);
            float sy = obj.getProperties().get("y", Float.class);
            player.setCenter(sx, sy);
            System.out.println("Player spawn OK: x=" + sx + ", y=" + sy);
        } catch (Exception e) {
            System.out.println("Erreur PlayerSpawn: " + e.getMessage());
            e.printStackTrace();
        }

        // Créer l'EnemyManager et charger les ennemis depuis la map
        enemyManager = new EnemyManager(collisions);
        enemyManager.loadEnemiesFromMap(map);

        healthBar = new HealthBar();
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        // Mettre à jour le joueur et les ennemis
        player.update(delta);
        enemyManager.update(delta, player);

        // Mise à jour de la caméra pour suivre le joueur
        float camX = player.getCenterX();
        float camY = player.getCenterY();
        float halfW = viewport.getWorldWidth() / 2f;
        float halfH = viewport.getWorldHeight() / 2f;

        if (camX < halfW) camX = halfW;
        if (camX > worldW - halfW) camX = worldW - halfW;
        if (camY < halfH) camY = halfH;
        if (camY > worldH - halfH) camY = worldH - halfH;

        camera.position.set(camX, camY, 0);
        camera.update();

        // Rendu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        enemyManager.draw(batch);  // Dessiner les ennemis d'abord
        player.draw(batch);        // Puis le joueur par-dessus
        batch.end();

        // Dessiner les barres de vie des ennemis
        shapeRenderer.setProjectionMatrix(camera.combined);
        enemyManager.drawHealthBars(shapeRenderer);

        // Dessiner la barre de vie APRÈS le batch
        healthBar.draw(camera, player);
    }

    @Override
    public void resize(int w, int h) {
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
        enemyManager.dispose();
        healthBar.dispose();
    }
}
