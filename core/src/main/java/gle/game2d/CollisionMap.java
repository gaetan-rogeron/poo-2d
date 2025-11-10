package gle.game2d;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class CollisionMap {
    private final TiledMapTileLayer layer;
    private final boolean layerBlocked;
    private final int tileSize = 16;

    public CollisionMap(TiledMap map, String layerName) {
        layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        if (layer == null) throw new IllegalArgumentException("Layer not found: " + layerName);
        MapProperties p = layer.getProperties();
        layerBlocked = p.containsKey("blocked") && Boolean.parseBoolean(p.get("blocked").toString());
    }

    public boolean isBlockedTile(int col, int row) {
        if (col < 0 || row < 0 || col >= layer.getWidth() || row >= layer.getHeight()) return true;
        TiledMapTileLayer.Cell cell = layer.getCell(col, row);
        if (cell == null) return false;
        if (layerBlocked) return true;
        TiledMapTile tile = cell.getTile();
        if (tile == null) return false;
        MapProperties tp = tile.getProperties();
        return tp.containsKey("blocked") && Boolean.parseBoolean(tp.get("blocked").toString());
    }

    public boolean collides(float x, float y, float w, float h) {
        int left   = (int)Math.floor(x / tileSize);
        int right  = (int)Math.floor((x + w - 1) / tileSize);
        int bottom = (int)Math.floor(y / tileSize);
        int top    = (int)Math.floor((y + h - 1) / tileSize);
        for (int c = left; c <= right; c++) {
            for (int r = bottom; r <= top; r++) {
                if (isBlockedTile(c, r)) return true;
            }
        }
        return false;
    }

    public int getTileSize() { return tileSize; }
}
