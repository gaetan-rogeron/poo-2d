package gle.game2d.enemy;

/**
 * Données de spawn pour un ennemi. Stocke simplement le type et la position.
 * Utilise String pour le type afin d'être extensible et orienté objet.
 */
class EnemySpawnData {
    private final String type;
    private final float x;
    private final float y;

    /**
     * Constructeur.
     * @param type Nom du type de l'ennemi
     * @param x Position X
     * @param y Position Y
     */
    public EnemySpawnData(String type, float x, float y) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Le type d'ennemi ne peut pas être vide");
        }
        this.type = type;
        this.x = x;
        this.y = y;
    }

    // Getters
    public String getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
