package gle.game2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import gle.game2d.Main;
import gle.game2d.zone.*;
import gle.game2d.collision.CollisionMap;
import gle.game2d.enemy.EnemyManager;
import gle.game2d.object.ObjectManager;
import gle.game2d.player.Player;

/** Écran principal du jeu. Gère le rendu de la carte, du joueur, des ennemis et de l'interface utilisateur. Implémente IZoneObserver pour réagir aux changements de zones. */
public class FirstScreen implements Screen, IZoneObserver {
    private final Main game;

    //Caméra et viewport
    private OrthographicCamera camera;
    private FitViewport viewport;

    //Carte
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private float worldW;
    private float worldH;

    //Rendu
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    //Entités du jeu
    private Player player;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private CollisionMap collisionMap;

    //Interface utilisateur
    private HealthBar healthBar;

    //Système de zones
    private ZoneManager zoneManager;
    private int currentTransitionIndex = 0;
    private final ITransitionStrategy[] transitionStrategies = {
        new SmoothStepTransition(),
        new LinearTransition(),
        new EaseInTransition(),
        new EaseOutTransition(),
        new EaseInOutTransition()
    };

    /** Constructeur de l'écran principal. */
    public FirstScreen(Main game) {
        this.game = game;
    }

    /** Appelé lorsque l'écran devient actif. Initialise tous les composants du jeu. */
    @Override
    public void show() {
        initializeMap();
        initializeCollisions();
        initializeZoneSystem();
        initializeCamera();
        initializeEnemies();
        initializeObjects();
        initializePlayer();
        initializeUI();

        System.out.println("FirstScreen initialisé");
        System.out.println("Appuyez sur T pour changer de stratégie de transition");
        System.out.println("Appuyez sur + ou - pour ajuster la vitesse de transition");
    }

    /** Initialise la carte Tiled et son renderer. */
    private void initializeMap() {
        map = new TmxMapLoader().load("maps/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        int mapWidthInTiles = layer.getWidth();
        int mapHeightInTiles = layer.getHeight();
        int tileSize = (int) layer.getTileWidth();

        worldW = mapWidthInTiles * tileSize;
        worldH = mapHeightInTiles * tileSize;

        System.out.println("Carte chargée: " + mapWidthInTiles + "x" + mapHeightInTiles +
            " tuiles (" + worldW + "x" + worldH + " pixels)");
    }

    /** Initialise le système de collisions. */
    private void initializeCollisions() {
        collisionMap = new CollisionMap(map, "Collision");
    }

    /** Initialise le système de zones et s'enregistre comme observateur. */
    private void initializeZoneSystem() {
        zoneManager = new ZoneManager(worldW, worldH, 2, 2);
        zoneManager.addObserver(this);
        zoneManager.setTransitionStrategy(transitionStrategies[currentTransitionIndex]);
        System.out.println("Système de zones initialisé: " + zoneManager);
    }

    /** Initialise la caméra et le viewport. */
    private void initializeCamera() {
        camera = new OrthographicCamera();
        float zoneWidth = zoneManager.getZoneWidth();
        float zoneHeight = zoneManager.getZoneHeight();
        viewport = new FitViewport(zoneWidth, zoneHeight, camera);
    }

    /** Initialise le joueur et charge sa position depuis la carte. */
    private void initializePlayer() {
        batch = new SpriteBatch();
        player = new Player(collisionMap);

        float playerStartX = loadPlayerSpawnPosition();
        float playerStartY = playerStartX == 0 ? 0 : loadPlayerSpawnY();

        if (playerStartX == 0 && playerStartY == 0) {
            playerStartX = zoneManager.getZoneWidth() / 2;
            playerStartY = zoneManager.getZoneHeight() / 2;
        }

        player.setCenter(playerStartX, playerStartY);
        zoneManager.initializeCamera(player.getCenterX(), player.getCenterY(), camera);
        camera.update();

        System.out.println("Joueur initialisé à la position: x=" + playerStartX + ", y=" + playerStartY);
    }

    /** Charge la position X du spawn du joueur depuis la carte. */
    private float loadPlayerSpawnPosition() {
        try {
            MapObject obj = map.getLayers().get("Object").getObjects().get("Player");
            return obj.getProperties().get("x", Float.class);
        } catch (Exception e) {
            System.out.println("Impossible de charger la position du joueur: " + e.getMessage());
            return 0;
        }
    }

    /** Charge la position Y du spawn du joueur depuis la carte. */
    private float loadPlayerSpawnY() {
        try {
            MapObject obj = map.getLayers().get("Object").getObjects().get("Player");
            return obj.getProperties().get("y", Float.class);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Initialise les ennemis et les charge depuis la carte. */
    private void initializeEnemies() {
        enemyManager = new EnemyManager(collisionMap);

        // Configurer les zones de l'EnemyManager (doit correspondre au ZoneManager)
        enemyManager.setZoneConfiguration(worldW, worldH, 2, 2);

        // Charger les ennemis depuis la carte
        enemyManager.loadEnemiesFromMap(map);

        // Enregistrer l'EnemyManager comme observateur des zones
        zoneManager.addObserver(enemyManager);

        System.out.println("EnemyManager initialisé et enregistré comme observateur des zones");
    }

    /** Initialise les objets collectables et les charge depuis la carte. */
    private void initializeObjects() {
        objectManager = new ObjectManager();
        objectManager.loadObjectsFromMap(map);
        System.out.println("ObjectManager initialisé");
    }

    /** Initialise l'interface utilisateur. */
    private void initializeUI() {
        healthBar = new HealthBar();
        shapeRenderer = new ShapeRenderer();
    }

    /** Rendu principal du jeu. Appelé à chaque frame. */
    @Override
    public void render(float delta) {
        handleInput();
        updateGameState(delta);
        renderScene();
    }

    /** Gère les entrées clavier pour les options de débogage. */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            cycleTransitionStrategy();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS) || Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            adjustTransitionSpeed(0.5f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            adjustTransitionSpeed(-0.5f);
        }
    }

    /** Change la stratégie de transition de manière cyclique. */
    private void cycleTransitionStrategy() {
        currentTransitionIndex = (currentTransitionIndex + 1) % transitionStrategies.length;
        ITransitionStrategy newStrategy = transitionStrategies[currentTransitionIndex];
        zoneManager.setTransitionStrategy(newStrategy);
        System.out.println("Stratégie de transition changée: " + newStrategy.getName());
    }

    /** Ajuste la vitesse de transition. */
    private void adjustTransitionSpeed(float adjustment) {
        float currentSpeed = zoneManager.getTransitionSpeed();
        float newSpeed = Math.max(0.5f, Math.min(10.0f, currentSpeed + adjustment));
        zoneManager.setTransitionSpeed(newSpeed);
        System.out.println("Vitesse de transition: " + String.format("%.1f", newSpeed));
    }

    /** Met à jour l'état du jeu. */
    private void updateGameState(float delta) {
        player.update(delta);
        enemyManager.update(delta, player);
        objectManager.update(delta, player);
        zoneManager.update(delta, player.getCenterX(), player.getCenterY(), camera);
        camera.update();
    }

    /** Effectue le rendu de la scène. */
    private void renderScene() {
        clearScreen();
        renderMap();
        renderEntities();
        renderHealthBars();
        renderUI();
    }

    /** Efface l'écran. */
    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /** Rend la carte Tiled. */
    private void renderMap() {
        renderer.setView(camera);
        renderer.render();
    }

    /** Rend les entités du jeu (objets, ennemis et joueur). */
    private void renderEntities() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        objectManager.draw(batch);  // Dessiner les objets en premier (derrière)
        enemyManager.draw(batch);
        player.draw(batch);
        batch.end();
    }

    /** Rend les barres de vie des ennemis. */
    private void renderHealthBars() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        enemyManager.drawHealthBars(shapeRenderer);
    }

    /** Rend l'interface utilisateur. */
    private void renderUI() {
        healthBar.draw(camera, player);
    }

    /** Appelé lors du redimensionnement de la fenêtre. */
    @Override
    public void resize(int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }

        float camX = camera.position.x;
        float camY = camera.position.y;

        viewport.update(w, h, false);

        camera.position.set(camX, camY, 0);
        camera.update();
    }

    /** Appelé lors de la mise en pause du jeu. */
    @Override
    public void pause() {
        // Rien à faire pour l'instant
    }

    /** Appelé lors de la reprise du jeu. */
    @Override
    public void resume() {
        // Rien à faire pour l'instant
    }

    /** Appelé lorsque l'écran n'est plus actif. */
    @Override
    public void hide() {
        // Retirer l'observateur du zone manager
        if (zoneManager != null) {
            zoneManager.removeObserver(this);
        }
    }

    /** Libère toutes les ressources utilisées. */
    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();
        if (batch != null) batch.dispose();
        if (player != null) player.dispose();
        if (enemyManager != null) enemyManager.dispose();
        if (objectManager != null) objectManager.dispose();
        if (healthBar != null) healthBar.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    //Implémentation de IZoneObserver

    /** Appelé lorsqu'une transition de zone commence. */
    @Override
    public void onTransitionStart(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY) {
        System.out.println("Début de transition: [" + fromZoneX + "," + fromZoneY +
            "] -> [" + toZoneX + "," + toZoneY + "]");
    }

    /** Appelé pendant la progression d'une transition de zone. */
    @Override
    public void onTransitionProgress(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY, float progress) {
        // Peut être utilisé pour des effets visuels pendant la transition
    }

    /** Appelé lorsqu'une transition de zone se termine. */
    @Override
    public void onTransitionEnd(int toZoneX, int toZoneY) {
        System.out.println("Fin de transition: arrivé à la zone [" + toZoneX + "," + toZoneY + "]");
    }

    /** Appelé lorsque le joueur entre dans une nouvelle zone sans transition. */
    @Override
    public void onZoneEnter(int zoneX, int zoneY) {
        System.out.println("Entrée dans la zone [" + zoneX + "," + zoneY + "]");
    }
}
