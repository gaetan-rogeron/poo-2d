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

    // Système de zones
    private ZoneManager zoneManager;

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

        // Initialiser le système de zones (2x2 = 4 zones)
        zoneManager = new ZoneManager(worldW, worldH, 2, 2);

        camera = new OrthographicCamera();

        // Le viewport doit correspondre à la taille d'UNE zone
        float zoneWidth = zoneManager.getZoneWidth();
        float zoneHeight = zoneManager.getZoneHeight();
        viewport = new FitViewport(zoneWidth, zoneHeight, camera);

        batch = new SpriteBatch();
        player = new Player(collisions);

        // Charger la position du joueur depuis Tiled
        float playerStartX = 0;
        float playerStartY = 0;

        try {
            MapObject obj = map.getLayers().get("Object").getObjects().get("Player");
            playerStartX = obj.getProperties().get("x", Float.class);
            playerStartY = obj.getProperties().get("y", Float.class);
            player.setCenter(playerStartX, playerStartY);
            System.out.println("Player spawn OK: x=" + playerStartX + ", y=" + playerStartY);
        } catch (Exception e) {
            System.out.println("Erreur PlayerSpawn: " + e.getMessage());
            e.printStackTrace();
            playerStartX = zoneManager.getZoneWidth() / 2;
            playerStartY = zoneManager.getZoneHeight() / 2;
            player.setCenter(playerStartX, playerStartY);
        }

        // Initialiser la caméra dans la zone du joueur (APRÈS avoir positionné le joueur)
        zoneManager.initializeCamera(player.getCenterX(), player.getCenterY(), camera);
        camera.update();

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

        // Mettre à jour le système de zones (déplace la caméra automatiquement)
        zoneManager.update(delta, player.getCenterX(), player.getCenterY(), camera);
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

        // Sauvegarder la position actuelle de la caméra
        float camX = camera.position.x;
        float camY = camera.position.y;

        // Mettre à jour le viewport SANS recentrer (false au lieu de true)
        viewport.update(w, h, false);

        // Restaurer la position de la caméra
        camera.position.set(camX, camY, 0);
        camera.update();
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
        shapeRenderer.dispose();
    }
}
