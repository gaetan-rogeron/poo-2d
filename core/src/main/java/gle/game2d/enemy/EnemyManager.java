package gle.game2d.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.collision.CollisionMap;
import gle.game2d.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Gestionnaire centralisé pour tous les ennemis. Responsable de la création, mise à jour, rendu et destruction des ennemis. */
public class EnemyManager {
    private final List<IEnemy> enemies;
    private final CollisionMap collisionMap;
    private final EnemyFactory factory;

    /** Constructeur. */
    public EnemyManager(CollisionMap collisionMap) {
        if (collisionMap == null) {
            throw new IllegalArgumentException("CollisionMap cannot be null");
        }

        this.collisionMap = collisionMap;
        this.enemies = new ArrayList<>();
        this.factory = new EnemyFactory();
    }

    /** Charge tous les ennemis depuis une TiledMap. */
    public void loadEnemiesFromMap(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("TiledMap cannot be null");
        }

        try {
            var objectLayer = map.getLayers().get("Object");
            if (objectLayer == null) {
                System.out.println("Warning: No 'Object' layer found in map");
                return;
            }

            for (MapObject obj : objectLayer.getObjects()) {
                trySpawnEnemyFromObject(obj);
            }

            System.out.println("Loaded " + enemies.size() + " enemies from map");
        } catch (Exception e) {
            System.err.println("Error loading enemies from map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Tente de créer un ennemi depuis un objet Tiled. */
    private void trySpawnEnemyFromObject(MapObject obj) {
        String name = obj.getName();
        if (name == null) return;

        try {
            float x = obj.getProperties().get("x", Float.class);
            float y = obj.getProperties().get("y", Float.class);

            EnemyFactory.EnemyType type = parseEnemyType(name);
            if (type != null) {
                spawnEnemy(type, x, y);
            }
        } catch (Exception e) {
            System.err.println("Failed to spawn enemy '" + name + "': " + e.getMessage());
        }
    }

    /** Convertit un nom d'objet en type d'ennemi. */
    private EnemyFactory.EnemyType parseEnemyType(String name) {
        try {
            return EnemyFactory.EnemyType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Pas un type d'ennemi valide
            return null;
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
    }

    /** Libère toutes les ressources. */
    public void dispose() {
        clear();
    }
}
