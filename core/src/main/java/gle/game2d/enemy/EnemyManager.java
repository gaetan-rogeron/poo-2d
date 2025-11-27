package gle.game2d.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.collision.CollisionMap;
import gle.game2d.player.Player;
import gle.game2d.zone.IZoneObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Gestionnaire centralisé pour tous les ennemis. Responsable de la création, mise à jour, rendu et destruction des ennemis. Implémente IZoneObserver pour spawner les ennemis par zone. */
public class EnemyManager implements IZoneObserver {
    private final List<IEnemy> enemies;
    private final Map<String, List<EnemySpawnData>> enemiesByZone;
    private final Set<String> activatedZones;
    private final CollisionMap collisionMap;
    private final EnemyFactory factory;

    // Configuration des zones (doit correspondre au ZoneManager)
    private float zoneWidth;
    private float zoneHeight;
    private int zonesX;
    private int zonesY;

    /** Constructeur. */
    public EnemyManager(CollisionMap collisionMap) {
        if (collisionMap == null) {
            throw new IllegalArgumentException("CollisionMap cannot be null");
        }

        this.collisionMap = collisionMap;
        this.enemies = new ArrayList<>();
        this.enemiesByZone = new HashMap<>();
        this.activatedZones = new HashSet<>();
        this.factory = new EnemyFactory();
    }

    /** Configure les dimensions des zones (doit correspondre au ZoneManager). */
    public void setZoneConfiguration(float worldWidth, float worldHeight, int zonesX, int zonesY) {
        this.zonesX = zonesX;
        this.zonesY = zonesY;
        this.zoneWidth = worldWidth / zonesX;
        this.zoneHeight = worldHeight / zonesY;
        System.out.println("EnemyManager: Configuration zones = " + zonesX + "x" + zonesY);
    }

    /** Charge tous les ennemis depuis une TiledMap et les organise par zone. */
    public void loadEnemiesFromMap(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("TiledMap cannot be null");
        }

        if (zoneWidth == 0 || zoneHeight == 0) {
            throw new IllegalStateException("Zone configuration must be set before loading enemies. Call setZoneConfiguration() first.");
        }

        try {
            var objectLayer = map.getLayers().get("Object");
            if (objectLayer == null) {
                System.out.println("Warning: No 'Object' layer found in map");
                return;
            }

            // Collecter et organiser tous les ennemis par zone
            int totalEnemies = 0;
            for (MapObject obj : objectLayer.getObjects()) {
                EnemySpawnData spawnData = tryCreateSpawnFromObject(obj);
                if (spawnData != null) {
                    String zoneKey = getZoneKey(spawnData.getX(), spawnData.getY());
                    enemiesByZone.computeIfAbsent(zoneKey, k -> new ArrayList<>()).add(spawnData);
                    totalEnemies++;
                }
            }

            // Afficher les statistiques
            System.out.println("Loaded " + totalEnemies + " enemies distributed across " + enemiesByZone.size() + " zones:");
            enemiesByZone.forEach((zoneKey, spawns) -> {
                System.out.println("  Zone " + zoneKey + ": " + spawns.size() + " enemies");
            });
        } catch (Exception e) {
            System.err.println("Error loading enemies from map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Calcule la clé de zone pour une position donnée. */
    private String getZoneKey(float x, float y) {
        int zoneX = Math.min((int)(x / zoneWidth), zonesX - 1);
        int zoneY = Math.min(zonesY - 1 - (int)(y / zoneHeight), zonesY - 1);
        return zoneX + "," + zoneY;
    }

    /** Tente de créer un EnemySpawnData depuis un objet Tiled. */
    private EnemySpawnData tryCreateSpawnFromObject(MapObject obj) {
        String name = obj.getName();
        if (name == null) return null;

        try {
            float x = obj.getProperties().get("x", Float.class);
            float y = obj.getProperties().get("y", Float.class);

            EnemyFactory.EnemyType type = parseEnemyType(name);
            if (type != null) {
                return new EnemySpawnData(type, x, y);
            }
        } catch (Exception e) {
            System.err.println("Failed to load enemy '" + name + "': " + e.getMessage());
        }
        return null;
    }

    /** Convertit un nom d'objet en type d'ennemi. */
    private EnemyFactory.EnemyType parseEnemyType(String name) {
        try {
            // Conversion directe
            return EnemyFactory.EnemyType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Essayer avec conversion de CamelCase vers SNAKE_CASE
            // Ex: "KingSlime" -> "KING_SLIME"
            String snakeCase = name.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
            try {
                return EnemyFactory.EnemyType.valueOf(snakeCase);
            } catch (IllegalArgumentException e2) {
                // Pas un type d'ennemi valide
                return null;
            }
        }
    }

    /** Crée et ajoute un ennemi à la position donnée. */
    public void spawnEnemy(EnemyFactory.EnemyType type, float x, float y) {
        IEnemy enemy = EnemyFactory.createEnemy(type, x, y, collisionMap);
        enemies.add(enemy);
        System.out.println(type + " spawned at: (" + x + ", " + y + ")");
    }

    /** Met à jour tous les ennemis. */
    public void update(float deltaTime, Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        // Mettre à jour tous les ennemis
        for (IEnemy enemy : enemies) {
            enemy.update(deltaTime, player);
        }

        // Vérifier les collisions avec l'attaque du joueur
        checkPlayerAttackCollisions(player);

        // Supprimer les ennemis morts
        removeDeadEnemies();
    }

    // Implémentation de IZoneObserver

    @Override
    public void onTransitionStart(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY) {
        // Pas besoin de faire quelque chose au début de la transition
    }

    @Override
    public void onTransitionProgress(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY, float progress) {
        // Pas besoin de faire quelque chose pendant la transition
    }

    @Override
    public void onTransitionEnd(int toZoneX, int toZoneY) {
        // Activer les ennemis de la zone de destination
        activateZone(toZoneX, toZoneY);
    }

    @Override
    public void onZoneEnter(int zoneX, int zoneY) {
        // Activer les ennemis de la zone (utilisé lors de l'initialisation)
        activateZone(zoneX, zoneY);
    }

    /** Active une zone et spawne tous ses ennemis. */
    private void activateZone(int zoneX, int zoneY) {
        String zoneKey = zoneX + "," + zoneY;

        // Vérifier si cette zone a déjà été activée
        if (activatedZones.contains(zoneKey)) {
            System.out.println("Zone [" + zoneX + ", " + zoneY + "] déjà activée");
            return;
        }

        // Spawner tous les ennemis de cette zone
        List<EnemySpawnData> spawnsInZone = enemiesByZone.get(zoneKey);
        if (spawnsInZone != null && !spawnsInZone.isEmpty()) {
            System.out.println("=== ACTIVATION ZONE [" + zoneX + ", " + zoneY + "] ===");
            System.out.println("Spawning " + spawnsInZone.size() + " enemies!");

            for (EnemySpawnData spawnData : spawnsInZone) {
                spawnEnemy(spawnData.getType(), spawnData.getX(), spawnData.getY());
            }

            activatedZones.add(zoneKey);
        } else {
            System.out.println("Zone [" + zoneX + ", " + zoneY + "] entrée - aucun ennemi à spawner");
            activatedZones.add(zoneKey);
        }
    }

    /** Vérifie les collisions entre l'attaque du joueur et les ennemis. */
    private void checkPlayerAttackCollisions(Player player) {
        Rectangle attackHitbox = player.getAttackHitbox();
        if (attackHitbox == null) return;

        int damage = player.getAttackDamage();

        for (IEnemy enemy : enemies) {
            if (enemy.isAlive() && attackHitbox.overlaps(enemy.getBounds())) {
                enemy.takeDamage(damage);
            }
        }
    }

    /** Supprime les ennemis morts de la liste. */
    private void removeDeadEnemies() {
        Iterator<IEnemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            IEnemy enemy = iterator.next();
            if (!enemy.isAlive()) {
                enemy.dispose();
                iterator.remove();
            }
        }
    }

    /** Dessine tous les ennemis. */
    public void draw(SpriteBatch batch) {
        if (batch == null) {
            throw new IllegalArgumentException("SpriteBatch cannot be null");
        }

        for (IEnemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    /** Dessine les barres de vie de tous les ennemis. */
    public void drawHealthBars(ShapeRenderer shapeRenderer) {
        if (shapeRenderer == null) {
            throw new IllegalArgumentException("ShapeRenderer cannot be null");
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (IEnemy enemy : enemies) {
            enemy.drawHealthBar(shapeRenderer);
        }
        shapeRenderer.end();
    }

    /** Retourne la liste en lecture seule des ennemis. */
    public List<IEnemy> getEnemies() {
        return new ArrayList<>(enemies); // Retourne une copie pour l'encapsulation
    }

    /** Retourne le nombre d'ennemis vivants. */
    public int getAliveEnemyCount() {
        return (int) enemies.stream().filter(IEnemy::isAlive).count();
    }

    /** Supprime tous les ennemis. */
    public void clear() {
        for (IEnemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
        enemiesByZone.clear();
        activatedZones.clear();
    }

    /** Libère toutes les ressources. */
    public void dispose() {
        clear();
    }
}
