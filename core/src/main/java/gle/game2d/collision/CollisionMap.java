package gle.game2d.collision;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/** Gère les collisions avec la carte de tuiles. Vérifie si une position donnée entre en collision avec des tuiles bloquantes. Applique le patron Facade pour simplifier l'accès aux données de la carte Tiled. */
public class CollisionMap {
    //Constantes
    private static final int DEFAULT_TILE_SIZE = 16;
    private static final String BLOCKED_PROPERTY = "blocked";

    //Attributs privés
    private final TiledMapTileLayer layer;
    private final boolean layerBlocked;
    private final int tileSize;

    /** Constructeur de la carte de collision. */
    public CollisionMap(TiledMap map, String layerName) {
        if (map == null) {
            throw new IllegalArgumentException("La carte ne peut pas être null");
        }
        if (layerName == null || layerName.isEmpty()) {
            throw new IllegalArgumentException("Le nom de la couche ne peut pas être vide");
        }

        this.layer = (TiledMapTileLayer) map.getLayers().get(layerName);

        if (this.layer == null) {
            throw new IllegalArgumentException("Couche introuvable: " + layerName);
        }

        MapProperties properties = layer.getProperties();
        this.layerBlocked = properties.containsKey(BLOCKED_PROPERTY)
            && Boolean.parseBoolean(properties.get(BLOCKED_PROPERTY).toString());

        this.tileSize = (int) layer.getTileWidth();

        System.out.println("CollisionMap initialisée: " + layerName
            + " (taille tuile: " + tileSize + ", bloquée: " + layerBlocked + ")");
    }

    /** Vérifie si une tuile à une position donnée est bloquante. */
    public boolean isBlockedTile(int col, int row) {
        // Hors limites = bloqué
        if (isOutOfBounds(col, row)) {
            return true;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(col, row);

        // Pas de cellule = pas de blocage
        if (cell == null) {
            return false;
        }

        // Si la couche entière est bloquée
        if (layerBlocked) {
            return true;
        }

        // Vérifier les propriétés de la tuile
        TiledMapTile tile = cell.getTile();
        if (tile == null) {
            return false;
        }

        MapProperties tileProperties = tile.getProperties();
        return tileProperties.containsKey(BLOCKED_PROPERTY)
            && Boolean.parseBoolean(tileProperties.get(BLOCKED_PROPERTY).toString());
    }

    /** Vérifie si une position est hors limites de la carte. */
    private boolean isOutOfBounds(int col, int row) {
        return col < 0 || row < 0 || col >= layer.getWidth() || row >= layer.getHeight();
    }

    /** Vérifie si une zone rectangulaire entre en collision avec des tuiles bloquantes. */
    public boolean collides(float x, float y, float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Largeur et hauteur doivent être positives");
        }

        // Convertir les coordonnées en indices de tuiles
        int leftCol = pixelToTileCoord(x);
        int rightCol = pixelToTileCoord(x + width - 1);
        int bottomRow = pixelToTileCoord(y);
        int topRow = pixelToTileCoord(y + height - 1);

        // Vérifier toutes les tuiles couvertes par la zone
        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = bottomRow; row <= topRow; row++) {
                if (isBlockedTile(col, row)) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Convertit une coordonnée en pixels en indice de tuile. */
    private int pixelToTileCoord(float pixelCoord) {
        return (int) Math.floor(pixelCoord / tileSize);
    }

    /** Convertit un indice de tuile en coordonnée en pixels. */
    public float tileToPixelCoord(int tileCoord) {
        return tileCoord * tileSize;
    }

    /** Obtient la taille d'une tuile. */
    public int getTileSize() {
        return tileSize;
    }

    /** Obtient la largeur de la carte en tuiles. */
    public int getMapWidthInTiles() {
        return layer.getWidth();
    }

    /** Obtient la hauteur de la carte en tuiles. */
    public int getMapHeightInTiles() {
        return layer.getHeight();
    }

    /** Obtient la largeur de la carte en pixels. */
    public float getMapWidthInPixels() {
        return layer.getWidth() * tileSize;
    }

    /** Obtient la hauteur de la carte en pixels. */
    public float getMapHeightInPixels() {
        return layer.getHeight() * tileSize;
    }

    /** Vérifie si la couche entière est bloquante. */
    public boolean isLayerBlocked() {
        return layerBlocked;
    }

    @Override
    public String toString() {
        return "CollisionMap{" +
            "width=" + getMapWidthInTiles() +
            ", height=" + getMapHeightInTiles() +
            ", tileSize=" + tileSize +
            ", layerBlocked=" + layerBlocked +
            '}';
    }
}
