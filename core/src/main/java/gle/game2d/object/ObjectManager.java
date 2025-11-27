package gle.game2d.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Gestionnaire centralisé pour tous les objets collectables du jeu. */
public class ObjectManager {
    private final List<ICollectable> objects;

    /** Constructeur. */
    public ObjectManager() {
        this.objects = new ArrayList<>();
    }

    /** Charge tous les objets depuis une TiledMap. */
    public void loadObjectsFromMap(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("TiledMap cannot be null");
        }

        try {
            var objectLayer = map.getLayers().get("Object");
            if (objectLayer == null) {
                System.out.println("Warning: No 'Object' layer found in map");
                return;
            }

            System.out.println("=== DEBUG ObjectManager ===");
            System.out.println("Scanning objects in map...");

            int potionCount = 0;
            int swordCount = 0;
            int totalObjects = 0;

            for (MapObject obj : objectLayer.getObjects()) {
                totalObjects++;
                String name = obj.getName();

                // Debug: afficher tous les objets trouvés
                System.out.println("  Object #" + totalObjects + ": name='" + name + "', class=" + obj.getClass().getSimpleName());

                if (name == null) continue;

                // Vérifier si c'est une potion
                if ("Potion".equalsIgnoreCase(name)) {
                    try {
                        // Obtenir les coordonnées depuis les propriétés
                        Float x = obj.getProperties().get("x", Float.class);
                        Float y = obj.getProperties().get("y", Float.class);

                        if (x == null || y == null) {
                            System.err.println("    -> Potion sans coordonnées x/y valides");
                            continue;
                        }

                        System.out.println("    -> Creating potion at (" + x + ", " + y + ")");
                        Potion potion = new Potion(x, y);
                        objects.add(potion);
                        potionCount++;
                        System.out.println("    -> Potion created successfully!");
                    } catch (Exception e) {
                        System.err.println("    -> Failed to load potion: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                // Vérifier si c'est une épée
                else if ("Sword".equalsIgnoreCase(name)) {
                    try {
                        // Obtenir les coordonnées depuis les propriétés
                        Float x = obj.getProperties().get("x", Float.class);
                        Float y = obj.getProperties().get("y", Float.class);

                        if (x == null || y == null) {
                            System.err.println("    -> Sword sans coordonnées x/y valides");
                            continue;
                        }

                        System.out.println("    -> Creating sword at (" + x + ", " + y + ")");
                        Sword sword = new Sword(x, y);
                        objects.add(sword);
                        swordCount++;
                        System.out.println("    -> Sword created successfully!");
                    } catch (Exception e) {
                        System.err.println("    -> Failed to load sword: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Total objects scanned: " + totalObjects);
            System.out.println("Loaded " + potionCount + " potions and " + swordCount + " swords from map");
            System.out.println("===========================");
        } catch (Exception e) {
            System.err.println("Error loading objects from map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Met à jour tous les objets et vérifie les collisions avec le joueur. */
    public void update(float deltaTime, Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        // Mettre à jour tous les objets
        for (ICollectable object : objects) {
            object.update(deltaTime);
        }

        // Vérifier les collisions avec le joueur
        checkPlayerCollisions(player);

        // Supprimer les objets collectés
        removeCollectedObjects();
    }

    /** Vérifie les collisions entre le joueur et les objets. */
    private void checkPlayerCollisions(Player player) {
        Rectangle playerBounds = new Rectangle(
            player.getCenterX() - 8, // Centré sur le joueur
            player.getCenterY() - 8,
            16,
            16
        );

        for (ICollectable object : objects) {
            if (!object.isCollected() && playerBounds.overlaps(object.getBounds())) {
                object.onCollect(player);
            }
        }
    }

    /** Supprime les objets collectés de la liste. */
    private void removeCollectedObjects() {
        Iterator<ICollectable> iterator = objects.iterator();
        while (iterator.hasNext()) {
            ICollectable object = iterator.next();
            if (object.isCollected()) {
                object.dispose();
                iterator.remove();
            }
        }
    }

    /** Dessine tous les objets. */
    public void draw(SpriteBatch batch) {
        if (batch == null) {
            throw new IllegalArgumentException("SpriteBatch cannot be null");
        }

        for (ICollectable object : objects) {
            object.draw(batch);
        }
    }

    /** Retourne le nombre d'objets restants. */
    public int getObjectCount() {
        return objects.size();
    }

    /** Supprime tous les objets. */
    public void clear() {
        for (ICollectable object : objects) {
            object.dispose();
        }
        objects.clear();
    }

    /** Libère toutes les ressources. */
    public void dispose() {
        clear();
    }
}
