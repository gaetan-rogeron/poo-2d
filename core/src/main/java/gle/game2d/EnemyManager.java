package gle.game2d;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyManager {
    private List<Enemy> enemies;
    private CollisionMap collisionMap;

    public EnemyManager(CollisionMap collisionMap) {
        this.collisionMap = collisionMap;
        this.enemies = new ArrayList<>();
    }

    public void loadEnemiesFromMap(TiledMap map) {
        try {
            for (MapObject obj : map.getLayers().get("Object").getObjects()) {
                String name = obj.getName();
                if (name == null) continue;

                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class);

                if (name.equals("Slime")) {
                    spawnSlime(x, y);
                    System.out.println("Slime spawned at: " + x + ", " + y);
                } else if (name.equals("Skeleton")) {
                    spawnSkeleton(x, y);
                    System.out.println("Skeleton spawned at: " + x + ", " + y);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading enemies from map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void spawnSlime(float x, float y) {
        enemies.add(new Slime(x, y, collisionMap));
    }

    public void spawnSkeleton(float x, float y) {
        enemies.add(new Skeleton(x, y, collisionMap));
    }

    public void update(float dt, Player player) {
        // Mettre à jour tous les ennemis
        for (Enemy enemy : enemies) {
            enemy.update(dt, player);
        }

        // Vérifier les collisions avec l'attaque du joueur
        Player.AttackHitbox hitbox = player.getAttackHitbox();
        if (hitbox != null) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() &&
                    hitbox.intersects(enemy.getX(), enemy.getY(),
                        enemy.getWidth(), enemy.getHeight())) {
                    enemy.takeDamage(hitbox.damage);
                }
            }
        }

        // Supprimer les ennemis morts
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (!enemy.isAlive()) {
                enemy.dispose();
                iterator.remove();
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    public void drawHealthBars(com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : enemies) {
            enemy.drawHealthBar(shapeRenderer);
        }
        shapeRenderer.end();
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
    }
}
